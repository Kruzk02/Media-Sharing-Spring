package com.app.DAO.subcomment;

import com.app.DAO.base.CRUDDao;
import com.app.Model.SortType;
import com.app.Model.SubComment;
import java.util.List;

public interface SubCommentDao extends CRUDDao<SubComment> {
  List<SubComment> findAllByCommentId(Long commentId, SortType sortType, int limit, int offset);
}
