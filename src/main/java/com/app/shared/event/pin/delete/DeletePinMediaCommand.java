package com.app.shared.event.pin.delete;

import java.time.LocalDateTime;
import org.springframework.modulith.NamedInterface;

@NamedInterface
public record DeletePinMediaCommand(Long mediaId, LocalDateTime createdAt) {}
