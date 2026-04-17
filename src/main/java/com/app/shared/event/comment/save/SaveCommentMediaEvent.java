package com.app.shared.event.comment.save;

import java.time.LocalDateTime;

import org.springframework.modulith.NamedInterface;
import org.springframework.web.multipart.MultipartFile;

@NamedInterface
public record SaveCommentMediaEvent(Long commentId, MultipartFile file, LocalDateTime createdAt) {}
