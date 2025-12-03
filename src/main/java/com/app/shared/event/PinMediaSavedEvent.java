package com.app.shared.event;

import java.time.LocalDateTime;

public record PinMediaSavedEvent(Long pinId, Long mediaId, LocalDateTime createdAt) {}
