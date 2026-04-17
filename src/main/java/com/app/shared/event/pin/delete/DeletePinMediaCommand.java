package com.app.shared.event.pin.delete;

import org.springframework.modulith.NamedInterface;

import java.time.LocalDateTime;

@NamedInterface
public record DeletePinMediaCommand(Long mediaId, LocalDateTime createdAt) {}
