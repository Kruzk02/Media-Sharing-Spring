package com.app.Service.impl;

import com.app.DAO.*;
import com.app.DTO.request.CreateCommentRequest;
import com.app.DTO.request.UpdatedCommentRequest;
import com.app.Model.*;
import com.app.Service.CommentService;
import com.app.exception.sub.CommentIsEmptyException;
import com.app.exception.sub.CommentNotFoundException;
import com.app.exception.sub.PinNotFoundException;
import com.app.exception.sub.UserNotMatchException;
import com.app.message.producer.NotificationEventProducer;
import com.app.storage.FileManager;
import com.app.storage.MediaManager;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Log4j2
@Service
@Qualifier("commentServiceImpl")
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentDao commentDao;
  private final UserDao userDao;
  private final PinDao pinDao;
  private final MediaDao mediaDao;
  private final HashtagDao hashtagDao;
  private final Map<Long, SseEmitter> emitters;
  private final NotificationEventProducer notificationEventProducer;

  private User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userDao.findUserByUsername(authentication.getName());
  }

  @Override
  public Comment save(CreateCommentRequest request) {
    if ((request.content() == null || request.content().trim().isEmpty())
        && (request.media() == null || request.media().isEmpty())) {
      throw new CommentIsEmptyException(
          "A comment must have either content or a media attachment.");
    }

    Media media = null;
    if (request.media() != null && !request.media().isEmpty()) {
      String filename = MediaManager.generateUniqueFilename(request.media().getOriginalFilename());
      String extension = MediaManager.getFileExtension(request.media().getOriginalFilename());

      media =
          mediaDao.save(
              Media.builder()
                  .url(filename)
                  .mediaType(MediaType.fromExtension(extension))
                  .status(Status.PENDING)
                  .build());

      Media finalMedia = media;

      FileManager.save(request.media(), filename, extension)
          .thenRunAsync(() -> mediaDao.updateStatus(finalMedia.getId(), Status.READY))
          .exceptionally(
              (err) -> {
                mediaDao.updateStatus(finalMedia.getId(), Status.FAILED);
                return null;
              });
    }

    User user = getAuthenticatedUser();

    Pin pin = pinDao.findById(request.pinId(), false);
    if (pin == null) {
      throw new PinNotFoundException("Pin not found with a id: " + request.pinId());
    }

    Comment savedComment =
        saveComment(request, pin.getId(), media == null ? 0 : media.getId(), user.getId());

    sendEvent("new-comment", savedComment);

    notificationEventProducer.send(
        Notification.builder()
            .userId(pin.getUserId())
            .message(user.getUsername() + " comment on your pin: " + request.pinId())
            .build());
    return savedComment;
  }

  @Override
  public Comment update(Long id, UpdatedCommentRequest request) {
    Comment comment = commentDao.findById(id, true);
    if (comment == null) {
      throw new CommentNotFoundException("Comment not found with a id: " + id);
    }

    if (!Objects.equals(getAuthenticatedUser().getId(), comment.getUserId())) {
      throw new UserNotMatchException("User not matching with a comment");
    }

    if ((request.content() == null || request.content().trim().isEmpty())
        && (request.media() == null || request.media().isEmpty())) {
      throw new CommentIsEmptyException(
          "A comment must have either content or a media attachment.");
    }

    if (request.media() != null && !request.media().isEmpty()) {
      handleUpdateMediaAsync(comment, request.media());
    }

    if (request.content() != null && !request.content().trim().isEmpty()) {
      comment.setContent(request.content());
    }

    if (request.tags() != null && !request.tags().isEmpty()) {
      comment.setHashtags(saveHashTag(request.tags()));
    }

    Comment updatedComment = commentDao.update(id, comment);

    sendEvent("updated-comment", updatedComment);

    return updatedComment;
  }

  private void handleUpdateMediaAsync(Comment comment, MultipartFile mediaFile) {
    String filename = MediaManager.generateUniqueFilename(mediaFile.getOriginalFilename());
    String extension = MediaManager.getFileExtension(mediaFile.getOriginalFilename());

    Media existingMedia = mediaDao.findByCommentId(comment.getId());
    Media mediaToUpdate;

    if (existingMedia != null) {
      mediaToUpdate = existingMedia;
    } else {
      mediaToUpdate =
          mediaDao.save(
              Media.builder()
                  .url(filename)
                  .mediaType(MediaType.fromExtension(extension))
                  .status(Status.PENDING)
                  .build());
    }

    CompletableFuture.runAsync(
            () -> {
              if (existingMedia != null) {
                String oldExtension = MediaManager.getFileExtension(existingMedia.getUrl());
                FileManager.delete(existingMedia.getUrl(), oldExtension);
              }
            })
        .thenRunAsync(() -> FileManager.save(mediaFile, filename, extension))
        .thenRunAsync(
            () -> {
              mediaToUpdate.setUrl(filename);
              mediaToUpdate.setMediaType(MediaType.fromExtension(extension));
              mediaToUpdate.setStatus(Status.READY);
              mediaDao.update(mediaToUpdate.getId(), mediaToUpdate);
            })
        .exceptionally(
            err -> {
              mediaDao.updateStatus(mediaToUpdate.getId(), Status.FAILED);
              return null;
            });

    comment.setMediaId(mediaToUpdate.getId());
  }

  private void sendEvent(String eventName, Comment comment) {
    SseEmitter emitter = emitters.get(comment.getPinId());
    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event().name(eventName).data(comment));
      } catch (IOException e) {
        emitter.completeWithError(e);
        emitters.remove(comment.getPinId());
      }
    }
  }

  @Transactional
  private List<Hashtag> saveHashTag(Set<String> tagsToFind) {
    Map<String, Hashtag> tags = hashtagDao.findByTag(tagsToFind);

    List<Hashtag> hashtags = new ArrayList<>();
    for (String tag : tagsToFind) {
      Hashtag hashtag = tags.get(tag);
      if (hashtag == null) {
        hashtag = hashtagDao.save(Hashtag.builder().tag(tag).build());
      }
      hashtags.add(hashtag);
    }
    return hashtags;
  }

  @Transactional
  private Comment saveComment(CreateCommentRequest request, Long pinId, Long mediaId, Long userId) {
    List<Hashtag> hashtags = saveHashTag(request.tags());

    return commentDao.save(
        Comment.builder()
            .content(request.content())
            .pinId(pinId)
            .mediaId(mediaId)
            .userId(userId)
            .hashtags(hashtags)
            .build());
  }

  @Override
  public SseEmitter createEmitter(long pinId) {
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    emitters.put(pinId, emitter);

    emitter.onCompletion(() -> emitters.remove(pinId));
    emitter.onTimeout(() -> emitters.remove(pinId));

    return emitter;
  }

  /**
   * Retrieves a comment with little details, using database and cache
   *
   * @param id The id of the comment to be found
   * @return A comment with specified id, either fetch from database or cache. If not comment are
   *     found, an exception is thrown
   */
  @Override
  public Comment findById(Long id, boolean fetchDetails) {
    return commentDao.findById(id, fetchDetails);
  }

  /**
   * Retrieves a list of comment associated with specific pin, using both database and cache.
   *
   * @param pinId The id of the pin whose comment are to be retrieved.
   * @param sortType The sort of the newest or oldest comment.
   * @param limit The maximum number of comment to be return.
   * @param offset The offset to paginate the comment result.
   * @return A list of the comment associated with specified pin ID, either fetch from database or
   *     cache. If no comment are found, an empty list is returned
   */
  @Override
  public List<Comment> findByPinId(Long pinId, SortType sortType, int limit, int offset) {
    var comments = commentDao.findByPinId(pinId, sortType, limit, offset);
    if (comments.isEmpty()) {
      return Collections.emptyList();
    }
    return comments;
  }

  @Override
  public List<Comment> findByHashtag(String tag, int limit, int offset) {
    var comments = commentDao.findByHashtag(tag, limit, offset);
    if (comments.isEmpty()) {
      return Collections.emptyList();
    }
    return comments;
  }

  /**
   * Delete comment by it ID.
   *
   * @param id The id of comment to be deleted.
   */
  @Override
  public void deleteById(Long id) {
    // Fetch the comment from database
    Comment comment = commentDao.findById(id, false);
    if (comment == null) {
      // Throw exception if not found
      throw new CommentNotFoundException("Comment not found with a id: " + id);
    }

    if (!Objects.equals(getAuthenticatedUser().getId(), comment.getUserId())) {
      // Throw exception if user not own comment
      throw new UserNotMatchException("Authenticated user does not own the comment.");
    }

    commentDao.deleteById(comment.getId());
  }
}
