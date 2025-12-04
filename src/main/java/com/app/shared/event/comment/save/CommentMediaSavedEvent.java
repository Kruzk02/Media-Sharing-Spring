package com.app.shared.event.comment.save;

import java.time.LocalDateTime;

public record CommentMediaSavedEvent(Long commentId, Long mediaId, LocalDateTime createdAt) {}
