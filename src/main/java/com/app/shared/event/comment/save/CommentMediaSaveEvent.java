package com.app.shared.event.comment.save;

import java.time.LocalDateTime;

public record CommentMediaSaveEvent(Long commentId, Long mediaId, LocalDateTime createdAt) {}
