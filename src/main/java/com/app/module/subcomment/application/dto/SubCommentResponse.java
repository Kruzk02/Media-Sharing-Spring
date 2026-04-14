package com.app.module.subcomment.application.dto;

import com.app.module.subcomment.domain.SubComment;
import java.time.LocalDateTime;

public record SubCommentResponse(
    Long id, String content, Long mediaId, Long commentId, Long userId, LocalDateTime createAt) {

  public static SubCommentResponse fromEntity(SubComment subComment) {
    return new SubCommentResponse(
        subComment.getId(),
        subComment.getContent(),
        subComment.getMediaId(),
        subComment.getCommentId(),
        subComment.getUserId(),
        subComment.getCreateAt());
  }
}
