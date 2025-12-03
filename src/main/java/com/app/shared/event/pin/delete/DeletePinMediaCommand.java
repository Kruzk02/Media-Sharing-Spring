package com.app.shared.event.pin.delete;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public record DeletePinMediaCommand(Long pinId, MultipartFile file, LocalDateTime createdAt) {}
