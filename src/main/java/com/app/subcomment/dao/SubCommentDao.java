package com.app.subcomment.dao;

import com.app.dao.base.CRUDDao;
import com.app.model.SortType;
import com.app.subcomment.model.SubComment;
import java.util.List;

public interface SubCommentDao extends CRUDDao<SubComment> {
  List<SubComment> findAllByCommentId(Long commentId, SortType sortType, int limit, int offset);
}
