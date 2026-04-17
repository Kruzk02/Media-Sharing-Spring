package com.app.shared.event.pin.save;

import java.time.LocalDateTime;

import org.springframework.modulith.NamedInterface;
import org.springframework.web.multipart.MultipartFile;

@NamedInterface
public record SavePinMediaCommand(Long pinId, MultipartFile file, LocalDateTime createdAt) {}
