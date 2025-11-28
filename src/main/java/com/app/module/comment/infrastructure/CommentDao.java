package com.app.module.comment.infrastructure;

import com.app.module.comment.domain.Comment;
import com.app.shared.dao.Creatable;
import com.app.shared.dao.Deletable;
import com.app.shared.dao.Updatable;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.util.List;

/** Data Access Object interface for managing Comment entities. */
public interface CommentDao extends Creatable<Comment>, Updatable<Comment>, Deletable {
  /**
   * Find a comment by its ID.
   *
   * @param id The ID of the comment
   * @param fetchDetails The basic or full details of the comment
   * @return The comment if found, or exception if not found
   */
  Comment findById(Long id, DetailsType detailsType);

  /**
   * Retrieves a list of comments by the ID of the associated pin.
   *
   * @param pinId The id of the pin
   * @param sortType The sort newest or oldest
   * @param limit The maximum number of result to return
   * @param offset the starting point for pagination
   * @return a list of comments
   */
  List<Comment> findByPinId(Long pinId, SortType sortType, int limit, int offset);

  /**
   * Retrieves a list of comments by the ID of the associated hashtag.
   *
   * @param tag The hashtag of the pin
   * @param limit The Maximum number of result to return
   * @param offset The starting point for pagination
   * @return a list of comments
   */
  List<Comment> findByHashtag(String tag, int limit, int offset);
}
