package com.app.shared.event.comment.save;

import java.time.LocalDateTime;

public record CommentMediaSaveFailedEvent(Long commentId, LocalDateTime createdAt) {}
