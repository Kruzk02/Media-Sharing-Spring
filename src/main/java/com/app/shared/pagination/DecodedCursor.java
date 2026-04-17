package com.app.shared.pagination;

import org.springframework.modulith.NamedInterface;

import java.time.LocalDateTime;

@NamedInterface
public record DecodedCursor(LocalDateTime dateTime, Long id) {}
