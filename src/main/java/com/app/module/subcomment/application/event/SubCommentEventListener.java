package com.app.module.subcomment.application.event;

import com.app.module.subcomment.domain.SubComment;
import com.app.module.subcomment.infrastructure.subcomment.SubCommentDao;
import com.app.shared.event.subcomment.save.SubCommentSavedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class SubCommentEventListener {
  private SubCommentDao subCommentDao;

  @EventListener
  public void handleSubCommentMediaSavedEvent(SubCommentSavedEvent event) {
    log.info(
        "Receive SubCommentMediaSavedEvent [subCommentId={}, mediaId={}, createdAt={}]",
        event.subCommentId(),
        event.mediaId(),
        event.createdAt());

    SubComment subComment = subCommentDao.findById(event.subCommentId());
    if (subComment == null) {
      log.warn("Sub comment {} not found for media {}", event.subCommentId(), event.mediaId());
      return;
    }

    subComment.setMediaId(event.mediaId());
    subCommentDao.update(subComment.getId(), subComment);
  }
}
