package com.app.module.pin.application.event;

import com.app.module.hashtag.domain.Hashtag;
import com.app.module.pin.infrastructure.PinDao;
import com.app.shared.event.hashtag.PinHashTagCreatedEvent;
import com.app.shared.event.hashtag.PinHashTagUpdatedEvent;
import com.app.shared.event.pin.save.PinMediaSavedEvent;
import com.app.shared.type.DetailsType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class PinEventListener {
  private final PinDao pinDao;

  @EventListener
  public void handlePinMediaSavedEvent(PinMediaSavedEvent event) {
    log.info(
        "Receive PinMediaSavedEvent [pinId={}, mediaId={}, createdAt={}]",
        event.pinId(),
        event.mediaId(),
        event.createdAt());

    var pin = pinDao.findById(event.pinId(), DetailsType.BASIC);
    if (pin == null) {
      log.warn("Pin {} not found for media {}", event.pinId(), event.mediaId());
      return;
    }

    pin.setMediaId(event.mediaId());
    pinDao.update(pin.getId(), pin);
  }

  @EventListener
  public void handlePinHashTagCreatedEvent(PinHashTagCreatedEvent event) {
    log.info(
        "Receive PinHashTagCreatedEvent [pinId={}, hashtags={}, createdAt={}]",
        event.pinId(),
        event.hashtags(),
        event.createdAt());
    addHashTagToPin(event.pinId(), event.hashtags());
  }

  @EventListener
  public void handlePinHashTagUpdatedEvent(PinHashTagUpdatedEvent event) {
    log.info(
        "Receive PinHashTagUpdatedEvent [pinId={}, hashtags={}, createdAt={}]",
        event.pinId(),
        event.hashtags(),
        event.createdAt());

    addHashTagToPin(event.pinId(), event.hashtags());
  }

  private void addHashTagToPin(Long id, List<Hashtag> hashtags) {
    var pin = pinDao.findById(id, DetailsType.BASIC);
    if (pin == null) {
      log.warn("Pin {} not found for hashtags {}", id, hashtags);
      return;
    }

    pin.setHashtags(hashtags);
    pinDao.update(pin.getId(), pin);
  }
}
