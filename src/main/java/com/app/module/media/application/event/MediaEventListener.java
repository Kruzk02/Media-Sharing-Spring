package com.app.module.media.application.event;

import com.app.module.media.domain.entity.Media;
import com.app.module.media.domain.status.MediaType;
import com.app.module.media.infrastructure.MediaDao;
import com.app.shared.event.UserMediaCreatedEvent;
import com.app.shared.event.UserUpdatedMediaEvent;
import com.app.shared.storage.FileManager;
import com.app.shared.storage.MediaManager;
import com.app.shared.type.Status;
import java.time.LocalDateTime;
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

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(UserUpdatedMediaEvent event) {
    log.info(
        "Receive UserUpdatedMediaEvent [userId={}, mediaId={}, filename={}, createAt={}]",
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
}
