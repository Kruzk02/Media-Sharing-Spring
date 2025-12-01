package com.app.shared.event;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public record UserUpdatedMediaEvent(
    Long userId, Long mediaId, MultipartFile file, LocalDateTime createAt) {}
