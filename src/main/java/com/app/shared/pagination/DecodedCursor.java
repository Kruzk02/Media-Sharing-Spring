package com.app.shared.pagination;

import java.time.Instant;
import org.springframework.modulith.NamedInterface;

@NamedInterface
public record DecodedCursor(Instant dateTime, Long id) {}
