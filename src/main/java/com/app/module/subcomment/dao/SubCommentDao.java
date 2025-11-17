package com.app.module.subcomment.dao;

import com.app.module.subcomment.model.SubComment;
import com.app.shared.dao.CRUDDao;
import com.app.shared.type.SortType;
import java.util.List;

public interface SubCommentDao extends CRUDDao<SubComment> {
  List<SubComment> findAllByCommentId(Long commentId, SortType sortType, int limit, int offset);
}
