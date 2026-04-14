package com.app.module.subcomment.application.dto;

import com.app.module.subcomment.domain.SubComment;
import com.app.shared.dto.response.CommentDTO;
import java.time.LocalDateTime;

public record SubCommentResponse(
    Long id,
    String content,
    Long mediaId,
    CommentDTO commentDTO,
    Long userId,
    LocalDateTime createAt) {

  public static SubCommentResponse fromEntity(SubComment subComment) {
    return new SubCommentResponse(
        subComment.getId(),
        subComment.getContent(),
        subComment.getMediaId(),
        new CommentDTO(subComment.getComment().getId(), subComment.getComment().getContent()),
        subComment.getUserId(),
        subComment.getCreateAt());
  }
}
