package com.app.shared.event.comment.update;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public record UpdateCommentMediaEvent(
    Long commentId, Long mediaId, MultipartFile file, LocalDateTime createdAt) {}
