package com.app.module.board.dao;

import com.app.module.board.model.Board;
import com.app.module.pin.domain.Pin;
import com.app.shared.dao.CRUDDao;
import java.util.List;

/** Interface for managing Board data access operations. */
public interface BoardDao extends CRUDDao<Board> {

  Board addPinToBoard(Pin pin, Board board);

  Board deletePinFromBoard(Pin pin, Board board);

  /**
   * Find all board by user id
   *
   * @param userId the user id of the board to be found
   * @return a list of board
   */
  List<Board> findAllByUserId(Long userId, int limit, int offset);
}
