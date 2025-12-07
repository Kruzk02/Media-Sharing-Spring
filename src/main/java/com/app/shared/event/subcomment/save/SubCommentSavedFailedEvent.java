package com.app.shared.event.subcomment.save;

import java.time.LocalDateTime;

public record SubCommentSavedFailedEvent(Long subCommentId, LocalDateTime createdAt) {
}
