package com.app.shared.event.subcomment.update;

import java.time.LocalDateTime;
import org.springframework.modulith.NamedInterface;
import org.springframework.web.multipart.MultipartFile;

@NamedInterface
public record UpdateSubCommentMediaEvent(
    Long subCommentId, Long mediaId, MultipartFile file, LocalDateTime createdAt) {}
