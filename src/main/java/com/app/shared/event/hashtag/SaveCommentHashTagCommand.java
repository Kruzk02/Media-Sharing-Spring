package com.app.shared.event.hashtag;

import org.springframework.modulith.NamedInterface;

import java.time.LocalDateTime;
import java.util.Set;

@NamedInterface
public record SaveCommentHashTagCommand(
    Long commentId, Set<String> hashtags, LocalDateTime createdAt) {}
