package com.app.module.comment.application.service;

import com.app.module.comment.application.dto.request.CreateCommentRequest;
import com.app.module.comment.application.dto.request.UpdatedCommentRequest;
import com.app.module.comment.domain.Comment;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface CommentService {
  Comment save(CreateCommentRequest request);

  Comment update(Long id, UpdatedCommentRequest request);

  SseEmitter createEmitter(long pinId);

  Comment findById(Long id, DetailsType detailsType);

  List<Comment> findByPinId(Long pinId, SortType sortType, int limit, int offset);

  List<Comment> findByHashtag(String tag, int limit, int offset);

  void deleteById(Long id);
}
