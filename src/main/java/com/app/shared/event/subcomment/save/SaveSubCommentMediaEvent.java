package com.app.shared.event.subcomment.save;

import java.time.LocalDateTime;
import org.springframework.modulith.NamedInterface;
import org.springframework.web.multipart.MultipartFile;

@NamedInterface
public record SaveSubCommentMediaEvent(
    Long subCommentId, MultipartFile file, LocalDateTime createdAt) {}
