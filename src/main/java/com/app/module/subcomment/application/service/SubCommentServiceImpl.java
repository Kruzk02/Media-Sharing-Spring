package com.app.module.subcomment.application.service;

import com.app.module.comment.dao.CommentDao;
import com.app.module.comment.dto.request.UpdatedCommentRequest;
import com.app.module.comment.model.Comment;
import com.app.module.media.dao.MediaDao;
import com.app.module.media.model.Media;
import com.app.module.media.model.MediaType;
import com.app.module.notification.model.Notification;
import com.app.module.subcomment.application.dto.CreateSubCommentRequest;
import com.app.module.subcomment.domain.SubComment;
import com.app.module.subcomment.domain.SubCommentNotFoundException;
import com.app.module.subcomment.infrastructure.subcomment.SubCommentDao;
import com.app.module.subcomment.internal.SubCommentValidator;
import com.app.module.user.domain.entity.User;
import com.app.module.user.infrastructure.user.UserDao;
import com.app.shared.exception.sub.UserNotMatchException;
import com.app.shared.message.producer.NotificationEventProducer;
import com.app.shared.storage.FileManager;
import com.app.shared.storage.MediaManager;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import com.app.shared.type.Status;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@Qualifier("subCommentServiceImpl")
public class SubCommentServiceImpl implements SubCommentService {

  private final SubCommentDao subCommentDao;
  private final CommentDao commentDao;
  private final UserDao userDao;
  private final MediaDao mediaDao;
  private final NotificationEventProducer notificationEventProducer;

  private User getAuthenticationUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userDao.findUserByUsername(authentication.getName());
  }

  @Override
  public SubComment save(CreateSubCommentRequest request) {

    SubCommentValidator.validateContent(request.content(), request.media());

    Comment comment = commentDao.findById(request.commentId(), DetailsType.BASIC);
    if (comment == null) {
      throw new SubCommentNotFoundException("Comment not found with a id: " + request.commentId());
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
              err -> {
                mediaDao.updateStatus(finalMedia.getId(), Status.FAILED);
                throw new CompletionException(err);
              });
    }

    SubComment savedSubComment = saveSubComment(request, comment, media);

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
  private SubComment saveSubComment(CreateSubCommentRequest request, Comment comment, Media media) {
    return subCommentDao.save(
        SubComment.builder()
            .content(request.content())
            .comment(comment)
            .user(getAuthenticationUser())
            .media(media)
            .build());
  }

  @Override
  public SubComment update(long id, UpdatedCommentRequest request) {

    SubComment subComment = subCommentDao.findById(id);
    if (subComment == null) {
      throw new SubCommentNotFoundException("Sub comment not found with a id: " + id);
    }

    if (!Objects.equals(getAuthenticationUser().getId(), subComment.getUser().getId())) {
      throw new UserNotMatchException("User does not match with sub comment");
    }

    SubCommentValidator.validateContent(request.content(), request.media());

    if (request.media() != null && !request.media().isEmpty()) {
      handleUpdateMediaAsync(subComment, request.media());
    }

    if (request.content() != null && !request.content().trim().isEmpty()) {
      subComment.setContent(request.content());
    }

    return subCommentDao.update(id, subComment);
  }

  private void handleUpdateMediaAsync(SubComment subComment, MultipartFile file) {
    String filename = MediaManager.generateUniqueFilename(file.getOriginalFilename());
    String extension = MediaManager.getFileExtension(file.getOriginalFilename());

    Media existingMedia = mediaDao.findById(subComment.getMedia().getId());
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
        .thenRunAsync(() -> FileManager.save(file, filename, extension))
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

    subComment.setMedia(mediaToUpdate);
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
  public void deleteById(long id) {
    SubComment subComment = subCommentDao.findById(id);
    if (subComment == null) {
      throw new SubCommentNotFoundException("Sub comment not found with id: " + id);
    }

    if (!Objects.equals(subComment.getUser().getId(), getAuthenticationUser().getId())) {
      throw new UserNotMatchException("Authenticated user does not own the sub comment");
    }

    subCommentDao.deleteById(id);
  }
}
