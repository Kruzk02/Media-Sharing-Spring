package com.app.DAO.board;

import com.app.DAO.base.Creatable;
import com.app.DAO.base.Deletable;
import com.app.DAO.base.Updatable;
import com.app.Model.Board;
import com.app.Model.Pin;
import java.util.List;

/** Interface for managing Board data access operations. */
public interface BoardDao extends Creatable<Board>, Updatable<Board>, Deletable {

  Board addPinToBoard(Pin pin, Board board);

  Board deletePinFromBoard(Pin pin, Board board);

  Board findById(Long id);

  /**
   * Find all board by user id
   *
   * @param userId the user id of the board to be found
   * @return a list of board
   */
  List<Board> findAllByUserId(Long userId, int limit, int offset);
}
