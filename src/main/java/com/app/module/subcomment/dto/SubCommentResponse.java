package com.app.module.subcomment.dto;

import com.app.module.subcomment.model.SubComment;
import com.app.shared.dto.response.CommentDTO;
import com.app.shared.dto.response.UserDTO;
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
