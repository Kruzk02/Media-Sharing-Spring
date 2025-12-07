package com.app.shared.event.subcomment.delete;

import java.time.LocalDateTime;

public record DeleteSubCommentMediaEvent(Long mediaId, LocalDateTime createdAt) {}
