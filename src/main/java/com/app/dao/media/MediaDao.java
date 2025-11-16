package com.app.dao.media;

import com.app.dao.base.CRUDDao;
import com.app.model.Media;
import com.app.model.Status;

/** Data Access Object interface for managing Media entities. */
public interface MediaDao extends CRUDDao<Media> {

  /**
   * Update status a existing media.
   *
   * @param id The ID of media to be updated
   * @param status The status to be updated.
   */
  void updateStatus(Long id, Status status);

  /**
   * Find a media by comment ID.
   *
   * @param commentId The ID of the comment
   * @return the media if found, or exception if not found
   */
  Media findByCommentId(Long commentId);
}
