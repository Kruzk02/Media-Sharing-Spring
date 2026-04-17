package com.app.shared.pagination;

import java.time.LocalDateTime;
import org.springframework.modulith.NamedInterface;

@NamedInterface
public record DecodedCursor(LocalDateTime dateTime, Long id) {}
