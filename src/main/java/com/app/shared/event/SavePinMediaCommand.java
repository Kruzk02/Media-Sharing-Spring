package com.app.shared.event;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public record SavePinMediaCommand(Long pinId, MultipartFile file, LocalDateTime createAt) { }
