package com.app.module.comment.application.service;

import com.app.module.comment.application.dto.request.CreateCommentRequest;
import com.app.module.comment.application.dto.request.UpdatedCommentRequest;
import com.app.module.comment.application.exception.CommentIsEmptyException;
import com.app.module.comment.domain.Comment;
import com.app.module.comment.domain.CommentNotFoundException;
import com.app.module.comment.infrastructure.CommentDao;
import com.app.module.hashtag.domain.Hashtag;
import com.app.module.hashtag.infrastructure.HashtagDao;
import com.app.module.notification.domain.Notification;
import com.app.module.pin.domain.Pin;
import com.app.module.pin.infrastructure.PinDao;
import com.app.module.user.domain.entity.User;
import com.app.module.user.infrastructure.user.UserDao;
import com.app.shared.event.comment.delete.DeleteCommentMediaEvent;
import com.app.shared.event.comment.save.SaveCommentMediaEvent;
import com.app.shared.event.comment.update.UpdateCommentMediaEvent;
import com.app.shared.event.hashtag.SaveCommentHashTagCommand;
import com.app.shared.event.hashtag.UpdateCommentHashtagCommand;
import com.app.shared.exception.sub.PinNotFoundException;
import com.app.shared.exception.sub.UserNotMatchException;
import com.app.shared.message.producer.NotificationEventProducer;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Qualifier("commentServiceImpl")
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentDao commentDao;
  private final UserDao userDao;
  private final PinDao pinDao;
  private final HashtagDao hashtagDao;
  private final Map<Long, SseEmitter> emitters;
  private final NotificationEventProducer notificationEventProducer;
  private final ApplicationEventPublisher eventPublisher;

  private User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userDao.findUserByUsername(Objects.requireNonNull(authentication).getName());
  }

  @Override
  @Transactional
  public Comment save(CreateCommentRequest request) {
    if ((request.content() == null || request.content().trim().isEmpty())
        && (request.media() == null || request.media().isEmpty())) {
      throw new CommentIsEmptyException(
          "A comment must have either content or a media attachment.");
    }

    User user = getAuthenticatedUser();

    Pin pin = pinDao.findById(request.pinId(), DetailsType.BASIC);
    if (pin == null) {
      throw new PinNotFoundException("Pin not found with a id: " + request.pinId());
    }

    Comment savedComment =
        commentDao.save(
            Comment.builder()
                .content(request.content())
                .pinId(request.pinId())
                .userId(user.getId())
                .build());

    if (request.media() != null && !request.media().isEmpty()) {
      eventPublisher.publishEvent(
          new SaveCommentMediaEvent(savedComment.getId(), request.media(), LocalDateTime.now()));
    }

    eventPublisher.publishEvent(
        new SaveCommentHashTagCommand(savedComment.getId(), request.tags(), LocalDateTime.now()));
    sendEvent("new-comment", savedComment);

    notificationEventProducer.send(
        Notification.builder()
            .userId(pin.getUserId())
            .message(user.getUsername() + " comment on your pin: " + request.pinId())
            .build());
    return savedComment;
  }

  @Override
  @Transactional
  public Comment update(Long id, UpdatedCommentRequest request) {
    Comment comment = commentDao.findById(id, DetailsType.BASIC);
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

    if (request.content() != null && !request.content().trim().isEmpty()) {
      comment.setContent(request.content());
    }

    Comment updatedComment = commentDao.update(id, comment);

    if (request.media() != null && !request.media().isEmpty()) {
      eventPublisher.publishEvent(
          new UpdateCommentMediaEvent(
              comment.getId(), comment.getMediaId(), request.media(), LocalDateTime.now()));
    }

    if (request.tags() != null && !request.tags().isEmpty()) {
        eventPublisher.publishEvent(
                new UpdateCommentHashtagCommand(updatedComment.getId(), request.tags(), LocalDateTime.now())
        );
    }

    sendEvent("updated-comment", updatedComment);

    return updatedComment;
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
  public Comment findById(Long id, DetailsType detailsType) {
    return commentDao.findById(id, detailsType);
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
  @Transactional
  public void deleteById(Long id) {
    // Fetch the comment from database
    Comment comment = commentDao.findById(id, DetailsType.BASIC);
    if (comment == null) {
      // Throw exception if not found
      throw new CommentNotFoundException("Comment not found with a id: " + id);
    }

    if (!Objects.equals(getAuthenticatedUser().getId(), comment.getUserId())) {
      // Throw exception if user not own comment
      throw new UserNotMatchException("Authenticated user does not own the comment.");
    }

    commentDao.deleteById(comment.getId());

    eventPublisher.publishEvent(
        new DeleteCommentMediaEvent(comment.getMediaId(), LocalDateTime.now()));
  }
}
