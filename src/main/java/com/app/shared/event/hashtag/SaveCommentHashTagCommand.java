package com.app.shared.event.hashtag;

import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.modulith.NamedInterface;

@NamedInterface
public record SaveCommentHashTagCommand(
    Long commentId, Set<String> hashtags, LocalDateTime createdAt) {}
