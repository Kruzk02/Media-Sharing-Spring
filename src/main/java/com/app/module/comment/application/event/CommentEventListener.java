package com.app.module.comment.application.event;

import com.app.module.comment.infrastructure.CommentDao;
import com.app.shared.event.comment.save.CommentMediaSavedEvent;
import com.app.shared.type.DetailsType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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
}
