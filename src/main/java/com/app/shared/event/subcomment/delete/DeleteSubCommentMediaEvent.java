package com.app.shared.event.subcomment.delete;

import org.springframework.modulith.NamedInterface;

import java.time.LocalDateTime;

@NamedInterface
public record DeleteSubCommentMediaEvent(Long mediaId, LocalDateTime createdAt) {}
