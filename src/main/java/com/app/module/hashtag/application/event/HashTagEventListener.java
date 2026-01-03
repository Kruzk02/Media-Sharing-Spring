package com.app.module.hashtag.application.event;

import com.app.module.hashtag.domain.Hashtag;
import com.app.module.hashtag.infrastructure.HashtagDao;
import com.app.shared.event.hashtag.PinHashTagCreatedEvent;
import com.app.shared.event.hashtag.SavePinHashTagCommand;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Log4j2
public class HashTagEventListener {
    private final HashtagDao hashtagDao;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSavePinHashTagCommand(SavePinHashTagCommand event) {
        log.info("Received SavePinHashTagCommand [pinId={}, hashtags={}, createdAt={}]", event.pinId(), event.hashtags(), event.createdAt());

        Map<String, Hashtag> tags = hashtagDao.findByTag(event.hashtags());

        List<Hashtag> hashtags = new ArrayList<>();
        for (String tag : event.hashtags()) {
            Hashtag hashtag = tags.get(tag);
            if (hashtag == null) {
                hashtag = hashtagDao.save(Hashtag.builder().tag(tag).build());
            }
            hashtags.add(hashtag);
        }

        eventPublisher.publishEvent(new PinHashTagCreatedEvent(event.pinId(), hashtags, LocalDateTime.now()));
    }
}
