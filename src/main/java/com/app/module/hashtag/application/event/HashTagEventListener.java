package com.app.module.hashtag.application.event;

import com.app.module.hashtag.domain.Hashtag;
import com.app.module.hashtag.infrastructure.HashtagDao;
import com.app.shared.event.hashtag.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
@Log4j2
public class HashTagEventListener {
  private final HashtagDao hashtagDao;
  private final ApplicationEventPublisher eventPublisher;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSavePinHashTagCommand(SavePinHashTagCommand event) {
    log.info(
        "Received SavePinHashTagCommand [pinId={}, hashtags={}, createdAt={}]",
        event.pinId(),
        event.hashtags(),
        event.createdAt());
    eventPublisher.publishEvent(
        new PinHashTagCreatedEvent(
            event.pinId(), findAndSaveHashtag(event.hashtags()), LocalDateTime.now()));
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUpdatePinHashtagCommand(UpdatePinHashTagCommand event) {
    log.info(
        "Received UpdatePinHashTagCommand [pinId={}, hashtags={}, createdAt={}]",
        event.pinId(),
        event.hashtags(),
        event.createdAt());
    eventPublisher.publishEvent(
        new PinHashTagUpdatedEvent(
            event.pinId(), findAndSaveHashtag(event.hashtags()), LocalDateTime.now()));
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSaveCommentHashtagCommand(SaveCommentHashTagCommand event) {
    log.info(
        "Received SaveCommentHashtagCommand [commentId={}, hashtags={}, createdAt={}]",
        event.commentId(),
        event.hashtags(),
        event.createdAt());
    eventPublisher.publishEvent(
        new CommentHashtagCreatedEvent(
            event.commentId(), findAndSaveHashtag(event.hashtags()), LocalDateTime.now()));
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUpdateCommentHashtagCommand(UpdateCommentHashtagCommand event) {
    log.info("Received UpdateCommentHashtagCommand [commentId={}, hashtags={}, createdAt={}]", event.commentId(), event.hashtags(), event.createdAt());
    eventPublisher.publishEvent(new CommentHashtagUpdatedEvent(event.commentId(), findAndSaveHashtag(event.hashtags()), LocalDateTime.now()));
  }

  private List<Hashtag> findAndSaveHashtag(Set<String> hashtagSet) {
    Map<String, Hashtag> tags = hashtagDao.findByTag(hashtagSet);

    List<Hashtag> hashtags = new ArrayList<>();
    for (String tag : hashtagSet) {
      var hashtag = tags.get(tag);
      if (hashtag == null) {
        hashtag = hashtagDao.save(Hashtag.builder().tag(tag).build());
      }
      hashtags.add(hashtag);
    }
    return hashtags;
  }
}
