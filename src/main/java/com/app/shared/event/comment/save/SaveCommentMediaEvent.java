package com.app.shared.event.comment.save;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public record SaveCommentMediaEvent(Long commentId, MultipartFile file, LocalDateTime createdAt) {}
