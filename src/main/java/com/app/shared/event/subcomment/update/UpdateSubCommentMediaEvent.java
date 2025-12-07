package com.app.shared.event.subcomment.update;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public record UpdateSubCommentMediaEvent(
    Long subCommentId, Long mediaId, MultipartFile file, LocalDateTime createdAt) {}
