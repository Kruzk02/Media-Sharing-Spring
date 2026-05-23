package com.app.module.notification.application.dto;

import java.time.Instant;

public record NotificationResponse(
    Long id, Long userId, String message, Boolean isRead, Instant createdAt) {}
