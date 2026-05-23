package com.app.module.pin.application.service;

import com.app.module.pin.application.dto.PinRequest;
import com.app.module.pin.application.exception.PinIsEmptyException;
import com.app.module.pin.domain.Pin;
import com.app.module.pin.infrastructure.dao.PinDao;
import com.app.shared.dto.response.CursorPage;
import com.app.shared.event.hashtag.SavePinHashTagCommand;
import com.app.shared.event.hashtag.UpdatePinHashTagCommand;
import com.app.shared.event.pin.delete.DeletePinMediaCommand;
import com.app.shared.event.pin.save.SavePinMediaCommand;
import com.app.shared.event.pin.update.UpdatePinMediaCommand;
import com.app.shared.exception.sub.PinNotFoundException;
import com.app.shared.exception.sub.UserNotMatchException;
import com.app.shared.gateway.UserGateway;
import com.app.shared.pagination.DecodedCursor;
import com.app.shared.pagination.KeysetCursorCodec;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
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
  private final UserGateway userGateway;
  private final ApplicationEventPublisher eventPublisher;

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public CursorPage<Pin> getAllPins(SortType sortType, int limit, String cursor) {
    return paginatePins(
        limit,
        cursor,
        (decodedCursor, queryLimit) ->
            pinDao.getAllPins(
                sortType,
                queryLimit,
                decodedCursor != null ? decodedCursor.dateTime() : null,
                decodedCursor != null ? decodedCursor.id() : null));
  }

  /** {@inheritDoc} */
  @Transactional(readOnly = true)
  @Override
  public CursorPage<Pin> getAllPinsByHashtag(String tag, int limit, String cursor) {
    return paginatePins(
        limit,
        cursor,
        (decodedCursor, queryLimit) ->
            pinDao.getAllPinsByHashtag(
                tag,
                queryLimit,
                decodedCursor != null ? decodedCursor.dateTime() : null,
                decodedCursor != null ? decodedCursor.id() : null));
  }

  private CursorPage<Pin> paginatePins(
      int limit, String cursor, BiFunction<DecodedCursor, Integer, List<Pin>> queryFn) {
    DecodedCursor decodedCursor = cursor != null ? KeysetCursorCodec.decode(cursor) : null;

    List<Pin> pins = queryFn.apply(decodedCursor, limit + 1);

    boolean hasNext = pins.size() > limit;

    if (hasNext) {
      pins = pins.subList(0, limit);
    }

    String encodedCursor = null;

    if (hasNext) {
      Pin lastPin = pins.getLast();
      encodedCursor = KeysetCursorCodec.encode(lastPin.getCreatedAt(), lastPin.getId());
    }

    return new CursorPage<>(pins, encodedCursor, hasNext);
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
            .userId(
                userGateway
                    .getUserByUsername(
                        Objects.requireNonNull(
                                SecurityContextHolder.getContext().getAuthentication())
                            .getName())
                    .id())
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

    Long userId =
        userGateway
            .getUserByUsername(
                Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication())
                    .getName())
            .id();

    if (userId == null || !Objects.equals(userId, existingPin.getUserId())) {
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

  @Override
  public List<Pin> findByIdIn(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    return pinDao.findByIdIn(ids);
  }

  /** {@inheritDoc} */
  @Transactional(readOnly = true)
  @Override
  public CursorPage<Pin> findPinByUserId(Long userId, int limit, String cursor) {
    return paginatePins(
        limit,
        cursor,
        (decodedCursor, queryLimit) ->
            pinDao.findPinByUserId(
                userId,
                queryLimit,
                decodedCursor != null ? decodedCursor.dateTime() : null,
                decodedCursor != null ? decodedCursor.id() : null));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void delete(Long id) {
    var userId =
        userGateway
            .getUserByUsername(
                Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication())
                    .getName())
            .id();

    Pin pin = pinDao.findById(id, DetailsType.BASIC);
    if (pin == null) {
      throw new PinNotFoundException("Pin not found with a id: " + id);
    }

    if (!Objects.equals(userId, pin.getUserId())) {
      throw new UserNotMatchException("Authenticated user does not own the pin");
    }
    pinDao.deleteById(id);

    eventPublisher.publishEvent(new DeletePinMediaCommand(pin.getMediaId(), LocalDateTime.now()));
  }
}
