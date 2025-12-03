package com.app.shared.event.pin.save;

import java.time.LocalDateTime;

public record PinMediaSavedEvent(Long pinId, Long mediaId, LocalDateTime createdAt) {}
