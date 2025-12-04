package com.app.shared.event.comment.delete;

import java.time.LocalDateTime;

public record DeleteCommentMediaEvent(Long mediaId, LocalDateTime createdAt) {}
