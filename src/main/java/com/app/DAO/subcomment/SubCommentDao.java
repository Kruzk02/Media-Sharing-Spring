package com.app.DAO.subcomment;

import com.app.DAO.base.Creatable;
import com.app.DAO.base.Deletable;
import com.app.DAO.base.Updatable;
import com.app.Model.SortType;
import com.app.Model.SubComment;
import java.util.List;

public interface SubCommentDao extends Creatable<SubComment>, Updatable<SubComment>, Deletable {
  List<SubComment> findAllByCommentId(Long commentId, SortType sortType, int limit, int offset);

  SubComment findById(Long id);
}
