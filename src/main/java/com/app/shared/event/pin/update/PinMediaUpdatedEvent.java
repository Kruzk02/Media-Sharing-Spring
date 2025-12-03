package com.app.shared.event.pin.update;

import java.time.LocalDateTime;

public record PinMediaUpdatedEvent(Long pinId, Long mediaId, LocalDateTime createdAt) {}
