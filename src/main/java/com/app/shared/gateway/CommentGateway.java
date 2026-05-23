package com.app.shared.gateway;

import com.app.shared.dto.response.CommentDTO;

public interface CommentGateway {
  CommentDTO getCommentById(Long commentId);
}
