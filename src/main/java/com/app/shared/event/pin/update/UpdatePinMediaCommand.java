package com.app.shared.event.pin.update;

import java.time.LocalDateTime;
import org.springframework.modulith.NamedInterface;
import org.springframework.web.multipart.MultipartFile;

@NamedInterface
public record UpdatePinMediaCommand(
    Long pinId, Long mediaId, MultipartFile file, LocalDateTime createdAt) {}
