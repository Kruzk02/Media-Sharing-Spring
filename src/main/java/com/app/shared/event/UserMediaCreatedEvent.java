package com.app.shared.event;

import java.time.LocalDateTime;

public record UserMediaCreatedEvent(Long userId, Long mediaId, LocalDateTime createAt) {}
