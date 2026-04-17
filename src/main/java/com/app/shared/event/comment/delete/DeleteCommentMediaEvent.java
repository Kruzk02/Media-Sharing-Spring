package com.app.shared.event.comment.delete;

import java.time.LocalDateTime;
import org.springframework.modulith.NamedInterface;

@NamedInterface
public record DeleteCommentMediaEvent(Long mediaId, LocalDateTime createdAt) {}
