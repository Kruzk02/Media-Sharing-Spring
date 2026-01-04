package com.app.module.subcomment.application.service;

import com.app.module.comment.application.dto.request.UpdatedCommentRequest;
import com.app.module.comment.domain.Comment;
import com.app.module.comment.infrastructure.CommentDao;
import com.app.module.notification.domain.Notification;
import com.app.module.subcomment.application.dto.CreateSubCommentRequest;
import com.app.module.subcomment.domain.SubComment;
import com.app.module.subcomment.domain.SubCommentNotFoundException;
import com.app.module.subcomment.infrastructure.subcomment.SubCommentDao;
import com.app.module.subcomment.internal.SubCommentValidator;
import com.app.module.user.domain.entity.User;
import com.app.module.user.infrastructure.user.UserDao;
import com.app.shared.event.subcomment.delete.DeleteSubCommentMediaEvent;
import com.app.shared.event.subcomment.save.SaveSubCommentMediaEvent;
import com.app.shared.event.subcomment.update.UpdateSubCommentMediaEvent;
import com.app.shared.exception.sub.UserNotMatchException;
import com.app.shared.message.producer.NotificationEventProducer;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.time.LocalDateTime;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Qualifier("subCommentServiceImpl")
@Log4j2
public class SubCommentServiceImpl implements SubCommentService {

  private final SubCommentDao subCommentDao;
  private final CommentDao commentDao;
  private final UserDao userDao;
  private final NotificationEventProducer notificationEventProducer;
  private final ApplicationEventPublisher eventPublisher;

  private User getAuthenticationUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userDao.findUserByUsername(Objects.requireNonNull(authentication).getName());
  }

  @Override
  @Transactional
  public SubComment save(CreateSubCommentRequest request) {
    SubCommentValidator.validateContent(request.content(), request.media());

    Comment comment = commentDao.findById(request.commentId(), DetailsType.BASIC);
    if (comment == null) {
      throw new SubCommentNotFoundException("Comment not found with a id: " + request.commentId());
    }

    SubComment savedSubComment = saveSubComment(request, comment);

    if (request.media() != null && !request.media().isEmpty()) {
      eventPublisher.publishEvent(
          new SaveSubCommentMediaEvent(
              savedSubComment.getId(), request.media(), LocalDateTime.now()));
    }

    notificationEventProducer.send(
        Notification.builder()
            .userId(comment.getUserId())
            .message(
                getAuthenticationUser().getUsername()
                    + " replies on your comment "
                    + comment.getId())
            .build());
    return savedSubComment;
  }

  @Transactional
  private SubComment saveSubComment(CreateSubCommentRequest request, Comment comment) {
    return subCommentDao.save(
        SubComment.builder()
            .content(request.content())
            .comment(comment)
            .user(getAuthenticationUser())
            .build());
  }

  @Override
  @Transactional
  public SubComment update(long id, UpdatedCommentRequest request) {

    SubComment subComment = subCommentDao.findById(id);
    if (subComment == null) {
      throw new SubCommentNotFoundException("Sub comment not found with a id: " + id);
    }

    if (!Objects.equals(getAuthenticationUser().getId(), subComment.getUser().getId())) {
      throw new UserNotMatchException("User does not match with sub comment");
    }

    SubCommentValidator.validateContent(request.content(), request.media());

    if (request.content() != null && !request.content().trim().isEmpty()) {
      subComment.setContent(request.content());
    }

    SubComment updatedSubComment = subCommentDao.update(id, subComment);

    if (request.media() != null && !request.media().isEmpty()) {
      eventPublisher.publishEvent(
          new UpdateSubCommentMediaEvent(
              updatedSubComment.getId(),
              updatedSubComment.getMediaId(),
              request.media(),
              LocalDateTime.now()));
    }
    return subComment;
  }

  @Override
  public SubComment findById(long id) {
    return subCommentDao.findById(id);
  }

  @Override
  public List<SubComment> findAllByCommentId(
      long commentId, SortType sortType, int limit, int offset) {
    List<SubComment> subComments =
        subCommentDao.findAllByCommentId(commentId, sortType, limit, offset);
    if (subComments.isEmpty()) {
      return Collections.emptyList();
    }
    return subComments;
  }

  @Override
  @Transactional
  public void deleteById(long id) {
    SubComment subComment = subCommentDao.findById(id);
    if (subComment == null) {
      throw new SubCommentNotFoundException("Sub comment not found with id: " + id);
    }

    if (!Objects.equals(subComment.getUser().getId(), getAuthenticationUser().getId())) {
      throw new UserNotMatchException("Authenticated user does not own the sub comment");
    }

    subCommentDao.deleteById(id);

    eventPublisher.publishEvent(
        new DeleteSubCommentMediaEvent(subComment.getMediaId(), LocalDateTime.now()));
  }
}
