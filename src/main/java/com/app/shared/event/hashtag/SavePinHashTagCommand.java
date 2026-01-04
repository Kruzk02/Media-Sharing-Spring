package com.app.shared.event.hashtag;

import java.time.LocalDateTime;
import java.util.Set;

public record SavePinHashTagCommand(Long pinId, Set<String> hashtags, LocalDateTime createdAt) {}
