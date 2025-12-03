package com.app.shared.event.pin.save;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public record SavePinMediaCommand(Long pinId, MultipartFile file, LocalDateTime createdAt) {}
