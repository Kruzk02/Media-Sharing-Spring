package com.app.shared.event;

import java.time.LocalDateTime;

public record PinMediaSaveFailedEvent(Long pinId, Long mediaId, LocalDateTime createdAt) {}
