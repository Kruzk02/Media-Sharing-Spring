package com.app.module.media.application.event;

import com.app.module.media.domain.entity.Media;
import com.app.module.media.domain.status.MediaType;
import com.app.module.media.infrastructure.dao.MediaDao;
import com.app.shared.event.UserMediaCreatedEvent;
import com.app.shared.event.UserUpdatedMediaEvent;
import com.app.shared.event.comment.delete.DeleteCommentMediaEvent;
import com.app.shared.event.comment.save.CommentMediaSaveFailedEvent;
import com.app.shared.event.comment.save.CommentMediaSavedEvent;
import com.app.shared.event.comment.save.SaveCommentMediaEvent;
import com.app.shared.event.comment.update.UpdateCommentMediaEvent;
import com.app.shared.event.pin.delete.DeletePinMediaCommand;
import com.app.shared.event.pin.save.PinMediaSaveFailedEvent;
import com.app.shared.event.pin.save.PinMediaSavedEvent;
import com.app.shared.event.pin.save.SavePinMediaCommand;
import com.app.shared.event.pin.update.UpdatePinMediaCommand;
import com.app.shared.event.subcomment.delete.DeleteSubCommentMediaEvent;
import com.app.shared.event.subcomment.save.SaveSubCommentMediaEvent;
import com.app.shared.event.subcomment.save.SubCommentSavedEvent;
import com.app.shared.event.subcomment.save.SubCommentSavedFailedEvent;
import com.app.shared.event.subcomment.update.UpdateSubCommentMediaEvent;
import com.app.module.media.infrastructure.storage.FileManager;
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
import org.springframework.web.multipart.MultipartFile;

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

    String filename = generateFilename(event.file());
    String extension = extractExtension(event.file());

    Media media = savePendingMedia(filename, extension);

    saveFileAsync(event.file(), filename, extension)
        .thenRunAsync(
            () ->
                markReadyAndPublish(
                    media,
                    new UserMediaCreatedEvent(event.userId(), media.getId(), LocalDateTime.now())))
        .exceptionally(
            _ -> {
              markFailed(media);
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

    String filename = generateFilename(event.file());
    String extension = extractExtension(event.file());

    Media media = savePendingMedia(filename, extension);

    saveFileAsync(event.file(), filename, extension)
        .thenRunAsync(
            () ->
                markReadyAndPublish(
                    media,
                    new PinMediaSavedEvent(event.pinId(), media.getId(), LocalDateTime.now())))
        .exceptionally(
            _ -> {
              markFailed(media);
              eventPublisher.publishEvent(
                  new PinMediaSaveFailedEvent(event.pinId(), LocalDateTime.now()));
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

    updateMediaFile(existingMedia, event.file());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDeletePinMediaCommand(DeletePinMediaCommand event) {
    log.info(
        "Receive DeletePinMediaCommand [mediaId={}, createdAt={}]",
        event.mediaId(),
        event.createdAt());

    Media existingMedia = mediaDao.findById(event.mediaId());
    if (existingMedia != null) {
      deleteMedia(existingMedia);
    }
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSaveCommentMediaEvent(SaveCommentMediaEvent event) {
    log.info(
        "Receive SaveCommentMediaEvent [commentId={}, filename={}, createdAt={}]",
        event.commentId(),
        event.file().getOriginalFilename(),
        event.createdAt());

    String filename = generateFilename(event.file());
    String extension = extractExtension(event.file());

    Media media = savePendingMedia(filename, extension);

    saveFileAsync(event.file(), filename, extension)
        .thenRunAsync(
            () ->
                markReadyAndPublish(
                    media,
                    new CommentMediaSavedEvent(
                        event.commentId(), media.getId(), LocalDateTime.now())))
        .exceptionally(
            _ -> {
              markFailed(media);
              eventPublisher.publishEvent(
                  new CommentMediaSaveFailedEvent(event.commentId(), LocalDateTime.now()));
              return null;
            });
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUpdateCommentMediaEvent(UpdateCommentMediaEvent event) {
    log.info(
        "Receive UpdateCommentMediaEvent [commentId={}, mediaId={}, filename={}, createdAt={}]",
        event.commentId(),
        event.mediaId(),
        event.file().getOriginalFilename(),
        event.createdAt());

    Media existingMedia = mediaDao.findById(event.mediaId());
    if (existingMedia == null) {
      log.warn("Media not found with id: {}", event.mediaId());
      return;
    }

    updateMediaFile(existingMedia, event.file());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDeleteCommentMediaEvent(DeleteCommentMediaEvent event) {
    log.info(
        "Receive DeleteCommentMediaEvent [mediaId={}, createdAt={}]",
        event.mediaId(),
        event.createdAt());
    Media existingMedia = mediaDao.findById(event.mediaId());
    if (existingMedia != null) {
      deleteMedia(existingMedia);
    }
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSaveSubCommentMediaEvent(SaveSubCommentMediaEvent event) {
    log.info(
        "Receive SaveSubCommentMediaEvent [commentId={}, file={}, createdAt={}]",
        event.subCommentId(),
        event.file(),
        event.createdAt());

    String filename = generateFilename(event.file());
    String extension = extractExtension(event.file());

    Media media = savePendingMedia(filename, extension);

    saveFileAsync(event.file(), filename, extension)
        .thenRunAsync(
            () ->
                markReadyAndPublish(
                    media,
                    new SubCommentSavedEvent(
                        event.subCommentId(), media.getId(), LocalDateTime.now())))
        .exceptionally(
            _ -> {
              markFailed(media);
              eventPublisher.publishEvent(
                  new SubCommentSavedFailedEvent(event.subCommentId(), LocalDateTime.now()));
              return null;
            });
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUpdateSubCommentMediaEvent(UpdateSubCommentMediaEvent event) {
    log.info(
        "Receive UpdateSubCommentMediaEvent [commentId={}, mediaId={}, file={}, createdAt={}]",
        event.subCommentId(),
        event.mediaId(),
        event.file(),
        event.createdAt());
    Media existingMedia = mediaDao.findById(event.mediaId());
    if (existingMedia == null) {
      log.warn("Media not found with id: {}", event.mediaId());
      return;
    }

    updateMediaFile(existingMedia, event.file());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDeleteSubCommentMediaEvent(DeleteSubCommentMediaEvent event) {
    log.info(
        "Receive DeleteSubCommentMediaEvent [mediaId={}, createdAt={}]",
        event.mediaId(),
        event.createdAt());
    Media existingMedia = mediaDao.findById(event.mediaId());
    if (existingMedia != null) {
      deleteMedia(existingMedia);
    }
  }

  private String generateFilename(MultipartFile file) {
    return MediaManager.generateUniqueFilename(file.getOriginalFilename());
  }

  private String extractExtension(MultipartFile file) {
    return MediaManager.getFileExtension(file.getOriginalFilename());
  }

  private Media savePendingMedia(String filename, String extension) {
    return mediaDao.save(
        Media.builder()
            .url(filename)
            .mediaType(MediaType.fromExtension(extension))
            .status(Status.PENDING)
            .build());
  }

  private Media updatePendingMedia(Media existing, String filename, String extension) {
    return mediaDao.update(
        existing.getId(),
        Media.builder()
            .id(existing.getId())
            .url(filename)
            .mediaType(MediaType.fromExtension(extension))
            .status(Status.PENDING)
            .build());
  }

  private CompletableFuture<Void> saveFileAsync(
      MultipartFile file, String filename, String extension) {
    return CompletableFuture.runAsync(() -> FileManager.save(file, filename, extension));
  }

  private void updateMediaFile(Media existingMedia, MultipartFile newFile) {
    String oldFilename = existingMedia.getUrl();
    String oldExt = MediaManager.getFileExtension(oldFilename);

    String newFilename = generateFilename(newFile);
    String newExt = extractExtension(newFile);

    saveFileAsync(newFile, newFilename, newExt)
        .thenRunAsync(
            () -> {
              existingMedia.setUrl(newFilename);
              existingMedia.setMediaType(MediaType.fromExtension(newExt));
              existingMedia.setStatus(Status.READY);
              mediaDao.update(existingMedia.getId(), existingMedia);

              scheduler.schedule(
                  () -> FileManager.delete(oldFilename, oldExt), 30, TimeUnit.MINUTES);
            })
        .exceptionally(
            _ -> {
              markFailed(existingMedia);
              return null;
            });
  }

  private void deleteMedia(Media media) {
    FileManager.delete(media.getUrl(), MediaManager.getFileExtension(media.getUrl()));
    mediaDao.deleteById(media.getId());
  }

  private void markReadyAndPublish(Media media, Object event) {
    mediaDao.updateStatus(media.getId(), Status.READY);
    eventPublisher.publishEvent(event);
  }

  private void markFailed(Media media) {
    mediaDao.updateStatus(media.getId(), Status.FAILED);
  }
}
