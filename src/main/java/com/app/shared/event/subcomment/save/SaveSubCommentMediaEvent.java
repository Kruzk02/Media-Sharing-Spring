package com.app.shared.event.subcomment.save;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public record SaveSubCommentMediaEvent(
    Long subCommentId, MultipartFile file, LocalDateTime createdAt) {}
