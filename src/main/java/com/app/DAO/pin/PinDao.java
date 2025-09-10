package com.app.DAO.pin;

import com.app.DAO.base.Creatable;
import com.app.DAO.base.Deletable;
import com.app.DAO.base.Updatable;
import com.app.Model.DetailsType;
import com.app.Model.Pin;
import com.app.Model.SortType;
import java.util.List;

/** Interface for managing Pin data access operations. */
public interface PinDao extends Creatable<Pin>, Updatable<Pin>, Deletable {

  /**
   * Retrieves all pins stored in the database.
   *
   * @return A list of all pins stored in the database.
   */
  List<Pin> getAllPins(SortType sortType, int limit, int offset);

  List<Pin> getAllPinsByHashtag(String tag, int limit, int offset);

  /**
   * Finds a basic detail pin by its id.
   *
   * @param id The id of the pin to be found.
   * @return The pin object if found, otherwise null.
   */
  Pin findById(Long id, DetailsType detailsType);

  /**
   * Find the pin that associated with a user id.
   *
   * @param userId The id of user.
   * @param limit The maximum number of result to return
   * @return a list of pin.
   */
  List<Pin> findPinByUserId(Long userId, int limit, int offset);
}
