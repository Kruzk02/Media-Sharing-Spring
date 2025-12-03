package com.app.shared.event.pin.update;

import java.time.LocalDateTime;

public record PinMediaUpdatedFailedEvent(Long pinId, Long mediaId, LocalDateTime createdAt) {}
