package com.app.module.notification.application.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long id, Long userId, String message, Boolean isRead, LocalDateTime createdAt) {}
