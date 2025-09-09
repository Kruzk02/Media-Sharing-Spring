package com.app.Service;

import com.app.DTO.request.CreateCommentRequest;
import com.app.DTO.request.UpdatedCommentRequest;
import com.app.Model.Comment;
import com.app.Model.DetailsType;
import com.app.Model.SortType;
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
