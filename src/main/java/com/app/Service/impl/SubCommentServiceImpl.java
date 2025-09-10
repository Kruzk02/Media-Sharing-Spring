package com.app.Service.impl;

import com.app.DAO.comment.CommentDao;
import com.app.DAO.MediaDao;
import com.app.DAO.SubCommentDao;
import com.app.DAO.UserDao;
import com.app.DTO.request.CreateSubCommentRequest;
import com.app.DTO.request.UpdatedCommentRequest;
import com.app.Model.*;
import com.app.Service.SubCommentService;
import com.app.exception.sub.CommentIsEmptyException;
import com.app.exception.sub.CommentNotFoundException;
import com.app.exception.sub.SubCommentNotFoundException;
import com.app.exception.sub.UserNotMatchException;
import com.app.message.producer.NotificationEventProducer;
import com.app.storage.FileManager;
import com.app.storage.MediaManager;
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
    if ((request.content() == null || request.content().trim().isEmpty())
        && (request.media() == null || request.media().isEmpty())) {
      throw new CommentIsEmptyException(
          "A comment must have either content or a media attachment.");
    }

    Comment comment = commentDao.findById(request.commentId(), DetailsType.BASIC);
    if (comment == null) {
      throw new CommentNotFoundException("Comment not found with a id: " + request.commentId());
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

    if ((request.content() == null || request.content().trim().isEmpty())
        && (request.media().isEmpty() || request.media().isEmpty())) {
      throw new CommentIsEmptyException(
          "A comment must have either content or a media attachment.");
    }

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
