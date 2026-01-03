package com.app.shared.event.hashtag;

import com.app.module.hashtag.domain.Hashtag;

import java.time.LocalDateTime;
import java.util.List;

public record PinHashTagUpdatedEvent(Long pinId, List<Hashtag> hashtags, LocalDateTime createdAt) {
}
