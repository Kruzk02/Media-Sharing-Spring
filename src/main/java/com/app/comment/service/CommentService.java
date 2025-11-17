package com.app.comment.service;

import com.app.comment.dto.request.CreateCommentRequest;
import com.app.comment.dto.request.UpdatedCommentRequest;
import com.app.comment.model.Comment;
import com.app.model.DetailsType;
import com.app.model.SortType;
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
