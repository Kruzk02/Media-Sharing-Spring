package com.app.module.pin.application.service;

import com.app.module.pin.application.dto.PinKeysetResponse;
import com.app.module.pin.application.dto.PinRequest;
import com.app.module.pin.domain.Pin;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.io.IOException;
import java.util.List;

/**
 * Service interface for managing Pin entities.
 *
 * <p>Provides operations for creating, updating, retrieving, listing, and deleting pins with
 * support for pagination, sorting, filtering, and detail levels.
 */
public interface PinService {

  /**
   * Retrieves all pins with keyset pagination.
   *
   * @param sortType the sorting strategy to apply
   * @param limit the maximum number of pins to return
   * @param cursor
   * @return a list of pins
   */
  PinKeysetResponse getAllPins(SortType sortType, int limit, String cursor);

  /**
   * Retrieves all pins associated with a specific hashtag.
   *
   * @param tag the hashtag to filter pins by (without '#')
   * @param limit the maximum number of pins to return
   * @param offset the starting position for pagination
   * @return a list of pins containing the given hashtag
   */
  List<Pin> getAllPinsByHashtag(String tag, int limit, int offset);

  /**
   * Create and saves a new pin.
   *
   * @param pinRequest the request object containing pin data
   * @return the saved pin entity
   */
  Pin save(PinRequest pinRequest);

  /**
   * Updates an existing pin.
   *
   * @param id the ID of the pin to update
   * @param pinRequest the request object containing updated pin data
   * @return the updated pin entity
   */
  Pin update(Long id, PinRequest pinRequest);

  /**
   * Finds a pin by its unique identifier.
   *
   * @param id the ID of the pin
   * @param detailsType specifies the level of detail to retrieve
   * @return the pin matching the given ID
   */
  Pin findById(Long id, DetailsType detailsType);

  /**
   * Retrieves all pins created by a specific user.
   *
   * @param userId the ID of the user
   * @param limit the maximum number of pins to return
   * @param offset the starting position for pagination
   * @return a list of pins created by the specific user
   */
  List<Pin> findPinByUserId(Long userId, int limit, int offset);

  /**
   * Deletes a pin by its ID
   *
   * @param id the ID of the pin to delete
   */
  void delete(Long id) throws IOException;
}
