package com.app.module.pin.application.service;

import com.app.module.pin.application.dto.PinRequest;
import com.app.module.pin.application.exception.PinIsEmptyException;
import com.app.module.pin.domain.Pin;
import com.app.module.pin.infrastructure.PinDao;
import com.app.module.user.domain.entity.User;
import com.app.module.user.infrastructure.user.UserDao;
import com.app.shared.event.hashtag.SavePinHashTagCommand;
import com.app.shared.event.hashtag.UpdatePinHashTagCommand;
import com.app.shared.event.pin.delete.DeletePinMediaCommand;
import com.app.shared.event.pin.save.SavePinMediaCommand;
import com.app.shared.event.pin.update.UpdatePinMediaCommand;
import com.app.shared.exception.sub.PinNotFoundException;
import com.app.shared.exception.sub.UserNotMatchException;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.time.LocalDateTime;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link PinService}.
 *
 * <p>This implementation retrieves and persists pin data using DAOs and publishes domain events for
 * media and hashtag processing.
 *
 * <p>Authentication is resolved from Spring Security context. All write operations are
 * transactional.
 */
@Slf4j
@Service
@Qualifier("pinServiceImpl")
@AllArgsConstructor
public class PinServiceImpl implements PinService {

  private final PinDao pinDao;
  private final UserDao userDao;
  private final ApplicationEventPublisher eventPublisher;

  private User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userDao.findUserByUsername(Objects.requireNonNull(authentication).getName());
  }

  /** {@inheritDoc} */
  @Transactional(readOnly = true)
  @Override
  public List<Pin> getAllPins(SortType sortType, int limit, int offset) {
    return pinDao.getAllPins(sortType, limit, offset);
  }

  /** {@inheritDoc} */
  @Transactional(readOnly = true)
  @Override
  public List<Pin> getAllPinsByHashtag(String tag, int limit, int offset) {
    return pinDao.getAllPinsByHashtag(tag, limit, offset);
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation requires a non-empty media file and publishes events for hashtag and
   * media
   *
   * @throws PinIsEmptyException if media file is not provided
   */
  @Override
  @Transactional
  public Pin save(PinRequest pinRequest) {
    if (pinRequest.file().isEmpty()) {
      throw new PinIsEmptyException("A pin must have file");
    }

    Pin pin =
        Pin.builder()
            .description(pinRequest.description())
            .userId(getAuthenticatedUser().getId())
            .build();
    Pin savedPin = pinDao.save(pin);

    eventPublisher.publishEvent(
        new SavePinHashTagCommand(savedPin.getId(), pinRequest.hashtags(), LocalDateTime.now()));
    eventPublisher.publishEvent(
        new SavePinMediaCommand(savedPin.getId(), pinRequest.file(), LocalDateTime.now()));
    return savedPin;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public Pin update(Long id, PinRequest pinRequest) {

    Pin existingPin = pinDao.findById(id, DetailsType.BASIC);

    if (existingPin == null) {
      throw new PinNotFoundException("Pin not found with a id: " + id);
    }

    if (getAuthenticatedUser() == null
        || !Objects.equals(getAuthenticatedUser().getId(), existingPin.getUserId())) {
      throw new UserNotMatchException("User does not matching with a pin owner");
    }

    if (pinRequest.file() != null && !pinRequest.file().isEmpty()) {
      eventPublisher.publishEvent(
          new UpdatePinMediaCommand(
              existingPin.getId(),
              existingPin.getMediaId(),
              pinRequest.file(),
              LocalDateTime.now()));
    }
    existingPin.setDescription(
        pinRequest.description() != null ? pinRequest.description() : existingPin.getDescription());
    var pin = pinDao.update(existingPin.getId(), existingPin);
    if (pinRequest.hashtags() != null && !pinRequest.hashtags().isEmpty()) {
      eventPublisher.publishEvent(
          new UpdatePinHashTagCommand(pin.getId(), pinRequest.hashtags(), LocalDateTime.now()));
    }
    return pin;
  }

  /** {@inheritDoc} */
  @Transactional(readOnly = true)
  @Override
  public Pin findById(Long id, DetailsType detailsType) {
    return pinDao.findById(id, detailsType);
  }

  /** {@inheritDoc} */
  @Transactional(readOnly = true)
  @Override
  public List<Pin> findPinByUserId(Long userId, int limit, int offset) {
    return pinDao.findPinByUserId(userId, limit, offset);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void delete(Long id) {
    var user = getAuthenticatedUser();

    Pin pin = pinDao.findById(id, DetailsType.BASIC);
    if (pin == null) {
      throw new PinNotFoundException("Pin not found with a id: " + id);
    }

    if (!Objects.equals(user.getId(), pin.getUserId())) {
      throw new UserNotMatchException("Authenticated user does not own the pin");
    }
    pinDao.deleteById(id);

    eventPublisher.publishEvent(new DeletePinMediaCommand(pin.getMediaId(), LocalDateTime.now()));
  }
}
