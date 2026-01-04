package com.app.shared.event.hashtag;

import java.time.LocalDateTime;
import java.util.Set;

public record SaveCommentHashTagCommand(
    Long commentId, Set<String> hashtags, LocalDateTime createdAt) {}
