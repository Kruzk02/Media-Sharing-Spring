package com.app.shared.event.subcomment.delete;

import java.time.LocalDateTime;
import org.springframework.modulith.NamedInterface;

@NamedInterface
public record DeleteSubCommentMediaEvent(Long mediaId, LocalDateTime createdAt) {}
