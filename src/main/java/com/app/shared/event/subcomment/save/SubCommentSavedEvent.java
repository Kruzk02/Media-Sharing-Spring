package com.app.shared.event.subcomment.save;

import java.time.LocalDateTime;

public record SubCommentSavedEvent(Long commentId, Long mediaId, LocalDateTime createdAt) {
}
