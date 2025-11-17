package com.app.subcomment.service;

import com.app.comment.dto.request.UpdatedCommentRequest;
import com.app.model.SortType;
import com.app.subcomment.dto.CreateSubCommentRequest;
import com.app.subcomment.model.SubComment;
import java.util.List;

public interface SubCommentService {
  SubComment save(CreateSubCommentRequest request);

  SubComment update(long id, UpdatedCommentRequest request);

  SubComment findById(long id);

  List<SubComment> findAllByCommentId(long commentId, SortType sortType, int limit, int offset);

  void deleteById(long id);
}
