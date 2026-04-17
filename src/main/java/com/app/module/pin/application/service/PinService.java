package com.app.module.pin.application.service;

import com.app.module.pin.application.dto.PinRequest;
import com.app.module.pin.domain.Pin;
import com.app.shared.dto.response.CursorPage;
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
   * Retrieves a paginated list of {@link Pin} objects using cursor-based pagination.
   *
   * @param sortType the sorting strategy applied to the result set (newest, oldest)
   * @param limit the maximum number of pins to return in a single page
   * @param cursor the pagination cursor representing the starting point for the next page; pass
   *     {@code null} to retrieve the first page
   * @return a {@link CursorPage} containing:
   *     <ul>
   *       <li>a list of {@link Pin} items
   *       <li>the next cursor value (if more data is available)
   *     </ul>
   */
  CursorPage<Pin> getAllPins(SortType sortType, int limit, String cursor);

  /**
   * Retrieves a paginated list of {@link Pin} objects associated with a specific hashtag using
   * cursor-based pagination.
   *
   * @param tag the hashtag to filter pins by (without '#')
   * @param limit the maximum number of pins to return in single page
   * @param cursor the pagination cursor representing the starting point for the next page; pass
   *     {@code null} to retrieve the first page
   * @return a {@link CursorPage} containing:
   *     <ul>
   *       <li>a list of {@link Pin} items
   *       <li>the next cursor value (if more data is available)
   *     </ul>
   */
  CursorPage<Pin> getAllPinsByHashtag(String tag, int limit, String cursor);

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
   * Retrieves multiple pins by its id.
   *
   * @param ids list of id.
   * @return a list of pins.
   */
  List<Pin> findByIdIn(List<Long> ids);

  /**
   * Retrieves all pins created by a specific user.
   *
   * @param userId the ID of the user
   * @param limit the maximum number of pins to return
   * @param cursor the pagination cursor representing the starting point for the next page; pass
   *     {@code null} to retrieve the first page
   * @return a {@link CursorPage} containing:
   *     <ul>
   *       <li>a list of {@link Pin} items
   *       <li>the next cursor value (if more data is available)
   *     </ul>
   */
  CursorPage<Pin> findPinByUserId(Long userId, int limit, String cursor);

  /**
   * Deletes a pin by its ID
   *
   * @param id the ID of the pin to delete
   */
  void delete(Long id) throws IOException;
}
