package com.app.service.impl;

import com.app.dao.hashtag.HashtagDao;
import com.app.dao.media.MediaDao;
import com.app.dao.pin.PinDao;
import com.app.dto.request.PinRequest;
import com.app.exception.sub.PinIsEmptyException;
import com.app.exception.sub.PinNotFoundException;
import com.app.exception.sub.UserNotMatchException;
import com.app.model.*;
import com.app.service.PinService;
import com.app.storage.FileManager;
import com.app.storage.MediaManager;
import com.app.user.dao.user.UserDao;
import com.app.user.model.User;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pin Service class responsible for handling operations relates to Pins.
 *
 * <p>This class interacts with the PinDaoImpl for data access, and utilizes ModelMapper for mapping
 * between DTOs and entity objects.
 */
@Slf4j
@Service
@Qualifier("pinServiceImpl")
@AllArgsConstructor
public class PinServiceImpl implements PinService {

  private final PinDao pinDao;
  private final UserDao userDao;
  private final MediaDao mediaDao;
  private final HashtagDao hashtagDao;

  private User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userDao.findUserByUsername(authentication.getName());
  }

  /**
   * Retrieves all pins.
   *
   * @return A List of all pins.
   */
  public List<Pin> getAllPins(SortType sortType, int limit, int offset) {
    return pinDao.getAllPins(sortType, limit, offset);
  }

  @Override
  public List<Pin> getAllPinsByHashtag(String tag, int limit, int offset) {
    return pinDao.getAllPinsByHashtag(tag, limit, offset);
  }

  @Override
  public Pin save(PinRequest pinRequest) {
    if (pinRequest.file().isEmpty()) {
      throw new PinIsEmptyException("A pin must have file");
    }

    String filename = MediaManager.generateUniqueFilename(pinRequest.file().getOriginalFilename());
    String extension = MediaManager.getFileExtension(pinRequest.file().getOriginalFilename());

    Media media =
        mediaDao.save(
            Media.builder()
                .url(filename)
                .mediaType(MediaType.fromExtension(extension))
                .status(Status.PENDING)
                .build());

    Pin pin = savePinAndHashTags(pinRequest, media.getId());

    FileManager.save(pinRequest.file(), filename, extension)
        .thenRunAsync(
            () -> {
              // TODO: add notify to the user
              mediaDao.updateStatus(media.getId(), Status.READY);
            })
        .exceptionally(
            (err) -> {
              mediaDao.updateStatus(media.getId(), Status.FAILED);

              pinDao.deleteById(pin.getId());

              FileManager.delete(filename, extension);
              return null;
            });
    return pin;
  }

  @Transactional
  private Pin savePinAndHashTags(PinRequest pinRequest, Long mediaId) {
    Set<String> tagsToFind = pinRequest.hashtags();
    Map<String, Hashtag> tags = hashtagDao.findByTag(tagsToFind);

    List<Hashtag> hashtags = new ArrayList<>();
    for (String tag : tagsToFind) {
      Hashtag hashtag = tags.get(tag);
      if (hashtag == null) {
        hashtag = hashtagDao.save(Hashtag.builder().tag(tag).build());
      }
      hashtags.add(hashtag);
    }

    Pin pin =
        Pin.builder()
            .description(pinRequest.description())
            .userId(getAuthenticatedUser().getId())
            .mediaId(mediaId)
            .hashtags(hashtags)
            .build();
    return pinDao.save(pin);
  }

  @Override
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
      Media existingMedia = mediaDao.findById(existingPin.getMediaId());
      String oldFilename = existingMedia.getUrl();
      String oldExtension = MediaManager.getFileExtension(oldFilename);

      String newFilename =
          MediaManager.generateUniqueFilename(pinRequest.file().getOriginalFilename());
      String newExtension = MediaManager.getFileExtension(pinRequest.file().getOriginalFilename());

      CompletableFuture.runAsync(() -> FileManager.delete(oldFilename, oldExtension))
          .thenRunAsync(() -> FileManager.save(pinRequest.file(), newFilename, newExtension))
          .thenRunAsync(
              () -> {
                existingMedia.setUrl(newFilename);
                existingMedia.setMediaType(MediaType.fromExtension(newExtension));
                existingMedia.setStatus(Status.READY);
                mediaDao.update(existingPin.getId(), existingMedia);
              })
          .exceptionally(
              err -> {
                mediaDao.updateStatus(existingMedia.getId(), Status.FAILED);
                return null;
              });
    }

    return updatePinAndHashTags(pinRequest, existingPin);
  }

  @Transactional
  private Pin updatePinAndHashTags(PinRequest pinRequest, Pin existingPin) {
    Set<String> tagsToFind = pinRequest.hashtags();
    Map<String, Hashtag> tags = hashtagDao.findByTag(tagsToFind);

    List<Hashtag> hashtags = new ArrayList<>();
    for (String tag : tagsToFind) {
      Hashtag hashtag = tags.get(tag);
      if (hashtag == null) {
        hashtag = hashtagDao.save(Hashtag.builder().tag(tag).build());
      }
      hashtags.add(hashtag);
    }

    existingPin.setDescription(
        pinRequest.description() != null ? pinRequest.description() : existingPin.getDescription());
    existingPin.setHashtags(hashtags);
    return pinDao.update(existingPin.getId(), existingPin);
  }

  /**
   * Retrieves a pin with little or full details, using database or cache
   *
   * @param id The id of the pin to be found.
   * @param fetchDetails The details of little or full details of the pin
   * @return A pin with specified id, either fetch from database or cache. If no pin are found, an
   *     exception is thrown
   */
  @Override
  public Pin findById(Long id, DetailsType detailsType) {
    return pinDao.findById(id, detailsType);
  }

  /**
   * Retrieves a list of pin associated with specific user, using both database and cache.
   *
   * @param userId The id of the user whose pin are to be retrieved.
   * @param limit The maximum number of pin to be return.
   * @param offset The offset to paginate the pin result.
   * @return A list of pin associated with specified user ID, either from fetch database or cache.
   *     If no pin are found, an empty list is returned
   */
  @Override
  public List<Pin> findPinByUserId(Long userId, int limit, int offset) {
    return pinDao.findPinByUserId(userId, limit, offset);
  }

  @Override
  public void delete(Long id) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = userDao.findUserByUsername(authentication.getName());

    Pin pin = pinDao.findById(id, DetailsType.BASIC);
    if (pin == null) {
      throw new PinNotFoundException("Pin not found with a id: " + id);
    }

    if (!Objects.equals(user.getId(), pin.getUserId())) {
      throw new UserNotMatchException("Authenticated user does not own the pin");
    }

    Media media = mediaDao.findById(pin.getMediaId());
    FileManager.delete(media.getUrl(), media.getMediaType().toString());
    mediaDao.deleteById(media.getId());

    pinDao.deleteById(id);
  }
}
