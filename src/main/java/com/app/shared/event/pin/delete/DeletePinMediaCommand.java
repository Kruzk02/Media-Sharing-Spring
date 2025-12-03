package com.app.shared.event.pin.delete;

import java.time.LocalDateTime;

public record DeletePinMediaCommand(Long mediaId, LocalDateTime createdAt) {}
