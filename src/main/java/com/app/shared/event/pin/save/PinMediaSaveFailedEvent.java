package com.app.shared.event.pin.save;

import java.time.LocalDateTime;

public record PinMediaSaveFailedEvent(Long pinId, LocalDateTime createdAt) {}
