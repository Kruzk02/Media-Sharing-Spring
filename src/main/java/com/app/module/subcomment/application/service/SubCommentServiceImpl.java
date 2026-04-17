package com.app.module.subcomment.application.service;

import com.app.module.comment.application.dto.request.UpdatedCommentRequest;
import com.app.module.notification.domain.Notification;
import com.app.module.subcomment.application.dto.CreateSubCommentRequest;
import com.app.module.subcomment.domain.SubComment;
import com.app.module.subcomment.domain.SubCommentNotFoundException;
import com.app.module.subcomment.infrastructure.subcomment.SubCommentDao;
import com.app.module.subcomment.internal.SubCommentValidator;
import com.app.shared.dto.response.CommentDTO;
import com.app.shared.dto.response.UserDTO;
import com.app.shared.event.subcomment.delete.DeleteSubCommentMediaEvent;
import com.app.shared.event.subcomment.save.SaveSubCommentMediaEvent;
import com.app.shared.event.subcomment.update.UpdateSubCommentMediaEvent;
import com.app.shared.exception.sub.UserNotMatchException;
import com.app.shared.gateway.CommentGateway;
import com.app.shared.gateway.UserGateway;
import com.app.shared.message.producer.NotificationEventProducer;
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
  private final CommentGateway commentGateway;
  private final UserGateway userGateway;
  private final NotificationEventProducer notificationEventProducer;
  private final ApplicationEventPublisher eventPublisher;

  private UserDTO getAuthenticationUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userGateway.getUserByUsername(Objects.requireNonNull(authentication).getName());
  }

  @Override
  @Transactional
  public SubComment save(CreateSubCommentRequest request) {
    SubCommentValidator.validateContent(request.content(), request.media());

    CommentDTO comment = commentGateway.getCommentById(request.commentId());
    if (comment == null) {
      throw new SubCommentNotFoundException("Comment not found with a id: " + request.commentId());
    }

    SubComment savedSubComment = saveSubComment(request, comment.id());

    if (request.media() != null && !request.media().isEmpty()) {
      eventPublisher.publishEvent(
          new SaveSubCommentMediaEvent(
              savedSubComment.getId(), request.media(), LocalDateTime.now()));
    }

    notificationEventProducer.send(
        Notification.builder()
            .userId(comment.userId())
            .message(
                getAuthenticationUser().username() + " replies on your comment " + comment.id())
            .build());
    return savedSubComment;
  }

  @Transactional
  protected SubComment saveSubComment(CreateSubCommentRequest request, Long commentId) {
    return subCommentDao.save(
        SubComment.builder()
            .content(request.content())
            .commentId(commentId)
            .userId(getAuthenticationUser().id())
            .build());
  }

  @Override
  @Transactional
  public SubComment update(long id, UpdatedCommentRequest request) {

    SubComment subComment = subCommentDao.findById(id);
    if (subComment == null) {
      throw new SubCommentNotFoundException("Sub comment not found with a id: " + id);
    }

    if (!Objects.equals(getAuthenticationUser().id(), subComment.getUserId())) {
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

    if (!Objects.equals(subComment.getUserId(), getAuthenticationUser().id())) {
      throw new UserNotMatchException("Authenticated user does not own the sub comment");
    }

    subCommentDao.deleteById(id);

    eventPublisher.publishEvent(
        new DeleteSubCommentMediaEvent(subComment.getMediaId(), LocalDateTime.now()));
  }
}
