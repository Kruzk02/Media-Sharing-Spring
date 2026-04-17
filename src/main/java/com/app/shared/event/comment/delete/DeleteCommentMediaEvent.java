package com.app.shared.event.comment.delete;

import org.springframework.modulith.NamedInterface;

import java.time.LocalDateTime;

@NamedInterface
public record DeleteCommentMediaEvent(Long mediaId, LocalDateTime createdAt) {}
