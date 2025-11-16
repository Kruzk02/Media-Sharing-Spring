package com.app.dto.response;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long id, Long userId, String message, Boolean isRead, LocalDateTime createdAt) {}
