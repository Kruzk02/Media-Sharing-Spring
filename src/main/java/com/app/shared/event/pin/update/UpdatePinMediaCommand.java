package com.app.shared.event.pin.update;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public record UpdatePinMediaCommand(
    Long pinId, Long mediaId, MultipartFile file, LocalDateTime createdAt) {}
