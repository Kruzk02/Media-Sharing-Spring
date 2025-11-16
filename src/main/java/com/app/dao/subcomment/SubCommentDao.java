package com.app.dao.subcomment;

import com.app.dao.base.CRUDDao;
import com.app.model.SortType;
import com.app.model.SubComment;
import java.util.List;

public interface SubCommentDao extends CRUDDao<SubComment> {
  List<SubComment> findAllByCommentId(Long commentId, SortType sortType, int limit, int offset);
}
