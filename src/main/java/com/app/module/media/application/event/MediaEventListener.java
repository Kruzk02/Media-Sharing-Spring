package com.app.module.media.application.event;

import com.app.module.media.domain.entity.Media;
import com.app.module.media.domain.status.MediaType;
import com.app.module.media.infrastructure.MediaDao;
import com.app.shared.event.UserMediaCreatedEvent;
import com.app.shared.event.UserUpdatedMediaEvent;
import com.app.shared.event.pin.delete.DeletePinMediaCommand;
import com.app.shared.event.pin.save.PinMediaSaveFailedEvent;
import com.app.shared.event.pin.save.PinMediaSavedEvent;
import com.app.shared.event.pin.save.SavePinMediaCommand;
import com.app.shared.event.pin.update.UpdatePinMediaCommand;
import com.app.shared.storage.FileManager;
import com.app.shared.storage.MediaManager;
import com.app.shared.type.Status;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
@Log4j2
public class MediaEventListener {

  private final MediaDao mediaDao;
  private final ApplicationEventPublisher eventPublisher;
  private static final ScheduledExecutorService scheduler =
      Executors.newSingleThreadScheduledExecutor();

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUserUpdatedMediaEvent(UserUpdatedMediaEvent event) {
    log.info(
        "Receive UserUpdatedMediaEvent [userId={}, mediaId={}, filename={}, createdAt={}]",
        event.userId(),
        event.mediaId(),
        event.file().getOriginalFilename(),
        event.createAt());

    Media existingMedia = mediaDao.findById(event.mediaId());

    Media media;
    String filename = MediaManager.generateUniqueFilename(event.file().getOriginalFilename());
    String extension = MediaManager.getFileExtension(event.file().getOriginalFilename());

    if (existingMedia == null || "default_profile_picture.png".equals(existingMedia.getUrl())) {
      media =
          mediaDao.save(
              Media.builder()
                  .url(filename)
                  .mediaType(MediaType.fromExtension(extension))
                  .status(Status.PENDING)
                  .build());
    } else {
      String oldExtension = MediaManager.getFileExtension(existingMedia.getUrl());
      media =
          mediaDao.update(
              existingMedia.getId(),
              Media.builder()
                  .id(existingMedia.getId())
                  .url(filename)
                  .mediaType(MediaType.fromExtension(extension))
                  .status(Status.PENDING)
                  .build());
      FileManager.delete(existingMedia.getUrl(), oldExtension);
    }

    FileManager.save(event.file(), filename, extension)
        .thenRunAsync(
            () -> {
              mediaDao.updateStatus(media.getId(), Status.READY);
              eventPublisher.publishEvent(
                  new UserMediaCreatedEvent(event.userId(), media.getId(), LocalDateTime.now()));
            })
        .exceptionally(
            err -> {
              log.error("File save failed for media {}", media.getId(), err);
              mediaDao.updateStatus(media.getId(), Status.FAILED);
              return null;
            });
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSavePinMediaCommand(SavePinMediaCommand event) {
    log.info(
        "Receive SavePinMediaCommand [pinId={}, filename={}, createdAt={}]",
        event.pinId(),
        event.file().getOriginalFilename(),
        event.createdAt());

    String filename = MediaManager.generateUniqueFilename(event.file().getOriginalFilename());
    String extension = MediaManager.getFileExtension(event.file().getOriginalFilename());

    Media media =
        mediaDao.save(
            Media.builder()
                .url(filename)
                .mediaType(MediaType.fromExtension(extension))
                .status(Status.PENDING)
                .build());

    FileManager.save(event.file(), filename, extension)
        .thenRunAsync(
            () -> {
              mediaDao.updateStatus(media.getId(), Status.READY);
              eventPublisher.publishEvent(
                  new PinMediaSavedEvent(event.pinId(), media.getId(), LocalDateTime.now()));
            })
        .exceptionally(
            (_) -> {
              mediaDao.updateStatus(media.getId(), Status.FAILED);

              eventPublisher.publishEvent(
                  new PinMediaSaveFailedEvent(event.pinId(), LocalDateTime.now()));

              FileManager.delete(filename, extension);
              return null;
            });
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUpdatePinMediaCommand(UpdatePinMediaCommand event) {
    log.info(
        "Receive UpdatePinMediaCommand [pinId={}, filename={}, createdAt={}]",
        event.pinId(),
        event.file().getOriginalFilename(),
        event.createdAt());

    Media existingMedia = mediaDao.findById(event.mediaId());
    if (existingMedia == null) {
      log.warn("Media not found with a id: {}", event.mediaId());
      return;
    }

    String oldFilename = existingMedia.getUrl();
    String oldExtension = MediaManager.getFileExtension(oldFilename);

    String newFileName = MediaManager.generateUniqueFilename(event.file().getOriginalFilename());
    String newExtension = MediaManager.getFileExtension(event.file().getOriginalFilename());

    CompletableFuture.runAsync(() -> FileManager.save(event.file(), newFileName, newExtension))
        .thenRunAsync(
            () -> {
              existingMedia.setUrl(newFileName);
              existingMedia.setMediaType(MediaType.fromExtension(newExtension));
              existingMedia.setStatus(Status.READY);
              mediaDao.update(existingMedia.getId(), existingMedia);

              scheduler.schedule(
                  () -> FileManager.delete(oldFilename, oldExtension), 30, TimeUnit.MINUTES);
            })
        .exceptionally(
            _ -> {
              mediaDao.updateStatus(existingMedia.getId(), Status.FAILED);
              return null;
            });
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDeletePinMediaCommand(DeletePinMediaCommand event) {
    log.info(
        "Receive DeletePinMediaCommand [mediaId={}, createdAt={}]",
        event.mediaId(),
        event.createdAt());

    Media existingMedia = mediaDao.findById(event.mediaId());
    FileManager.delete(existingMedia.getUrl(), MediaManager.getFileExtension(existingMedia.getUrl()));
    mediaDao.deleteById(existingMedia.getId());
  }
}
