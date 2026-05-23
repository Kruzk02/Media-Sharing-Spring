package com.app.module.board.infrastructure;

import com.app.module.board.domain.Board;
import com.app.shared.dao.CRUDDao;
import java.util.List;

/** Interface for managing Board data access operations. */
public interface BoardDao extends CRUDDao<Board> {

  Board addPinToBoard(Long pinId, Board board);

  Board deletePinFromBoard(Long pinId, Board board);

  /**
   * Find all board by user id
   *
   * @param userId the user id of the board to be found
   * @return a list of board
   */
  List<Board> findAllByUserId(Long userId, int limit, int offset);
}
