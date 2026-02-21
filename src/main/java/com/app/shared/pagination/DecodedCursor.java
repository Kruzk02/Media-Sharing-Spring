package com.app.shared.pagination;

import java.time.LocalDateTime;

public record DecodedCursor(LocalDateTime dateTime, Long id) {}
