package com.app.shared.event.comment.update;

import java.time.LocalDateTime;

import org.springframework.modulith.NamedInterface;
import org.springframework.web.multipart.MultipartFile;

@NamedInterface
public record UpdateCommentMediaEvent(
    Long commentId, Long mediaId, MultipartFile file, LocalDateTime createdAt) {}
