package com.app.module.subcomment.service;

import com.app.module.comment.dto.request.UpdatedCommentRequest;
import com.app.module.subcomment.dto.CreateSubCommentRequest;
import com.app.module.subcomment.model.SubComment;
import com.app.shared.type.SortType;
import java.util.List;

public interface SubCommentService {
  SubComment save(CreateSubCommentRequest request);

  SubComment update(long id, UpdatedCommentRequest request);

  SubComment findById(long id);

  List<SubComment> findAllByCommentId(long commentId, SortType sortType, int limit, int offset);

  void deleteById(long id);
}
