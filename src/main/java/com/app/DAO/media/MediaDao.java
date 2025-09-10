package com.app.DAO.media;

import com.app.DAO.base.Creatable;
import com.app.DAO.base.Deletable;
import com.app.DAO.base.Updatable;
import com.app.Model.Media;
import com.app.Model.Status;

/** Data Access Object interface for managing Media entities. */
public interface MediaDao extends Creatable<Media>, Updatable<Media>, Deletable {

  /**
   * Update status a existing media.
   *
   * @param id The ID of media to be updated
   * @param status The status to be updated.
   */
  void updateStatus(Long id, Status status);

  /**
   * Find a media by its ID.
   *
   * @param id The ID of the media
   * @return The media if found, or exception if not found.
   */
  Media findById(Long id);

  /**
   * Find a media by comment ID.
   *
   * @param commentId The ID of the comment
   * @return the media if found, or exception if not found
   */
  Media findByCommentId(Long commentId);
}
