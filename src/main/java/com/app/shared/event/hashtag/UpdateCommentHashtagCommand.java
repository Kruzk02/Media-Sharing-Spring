package com.app.shared.event.hashtag;

import java.time.LocalDateTime;
import java.util.Set;

public record UpdateCommentHashtagCommand(
    Long commentId, Set<String> hashtags, LocalDateTime createdAt) {}
