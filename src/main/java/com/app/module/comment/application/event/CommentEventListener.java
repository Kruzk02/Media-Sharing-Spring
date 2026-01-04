package com.app.module.comment.application.event;

import com.app.module.comment.infrastructure.CommentDao;
import com.app.module.hashtag.domain.Hashtag;
import com.app.shared.event.comment.save.CommentMediaSavedEvent;
import com.app.shared.event.hashtag.CommentHashtagCreatedEvent;
import com.app.shared.event.hashtag.CommentHashtagUpdatedEvent;
import com.app.shared.type.DetailsType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Log4j2
public class CommentEventListener {
  private final CommentDao commentDao;

  @EventListener
  public void handleCommentMediaSavedEvent(CommentMediaSavedEvent event) {
    log.info(
        "Receive CommentMediaSavedEvent [commentId={}, mediaId={}, createdAt={}]",
        event.commentId(),
        event.mediaId(),
        event.createdAt());

    var comment = commentDao.findById(event.commentId(), DetailsType.BASIC);
    if (comment == null) {
      log.warn("Comment {} not found for media {}", event.commentId(), event.mediaId());
      return;
    }

    comment.setMediaId(event.mediaId());
    commentDao.update(comment.getId(), comment);
  }

  @EventListener
  public void handleCommentHashtagSavedEvent(CommentHashtagCreatedEvent event) {
    log.info(
        "Receive CommentHashtagCreatedEvent [commentId={}, hashtags={}, createdAt={}]",
        event.commentId(),
        event.hashtags(),
        event.createdAt());
    addHashtagToComment(event.commentId(), event.hashtags());
  }

  @EventListener
  public void handleCommentHashtagUpdatedEvent(CommentHashtagUpdatedEvent event) {
    log.info(
            "Receive CommentHashtagUpdatedEvent [commentId={}, hashtags={}, createdAt={}]",
            event.commentId(),
            event.hashtags(),
            event.createdAt());
    addHashtagToComment(event.commentId(), event.hashtags());
  }

  private void addHashtagToComment(Long id, List<Hashtag> hashtags) {
    var comment = commentDao.findById(id, DetailsType.BASIC);
    if (comment == null) {
      log.warn("Comment {} not found for hashtags {}", id, hashtags);
      return;
    }

    comment.setHashtags(hashtags);
    commentDao.update(comment.getId(), comment);
  }
}
