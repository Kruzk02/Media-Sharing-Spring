package com.app.module.pin.infrastructure;

import com.app.module.pin.domain.Pin;
import com.app.shared.dao.Creatable;
import com.app.shared.dao.Deletable;
import com.app.shared.dao.Updatable;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.time.LocalDateTime;
import java.util.List;

/** Interface for managing Pin data access operations. */
public interface PinDao extends Creatable<Pin>, Updatable<Pin>, Deletable {

  /**
   * Retrieves all pins stored in the database.
   *
   * @return A list of all pins stored in the database.
   */
  List<Pin> getAllPins(SortType sortType, int limit, LocalDateTime dateTime, Long id);

  /**
   * Retrieves all pins by hash tag stored in the database.
   *
   * @return A list of all pins stored in the database.
   */
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
