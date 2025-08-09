package com.app.DTO.response;

import com.app.Model.SubComment;
import java.time.LocalDateTime;

public record SubCommentResponse(
    Long id,
    String content,
    long mediaId,
    CommentDTO commentDTO,
    UserDTO userDTO,
    LocalDateTime createAt) {

  public static SubCommentResponse fromEntity(SubComment subComment) {
    return new SubCommentResponse(
        subComment.getId(),
        subComment.getContent(),
        subComment.getMedia().getId(),
        new CommentDTO(subComment.getComment().getId(), subComment.getComment().getContent()),
        new UserDTO(subComment.getUser().getId(), subComment.getUser().getUsername()),
        subComment.getCreateAt());
  }
}
