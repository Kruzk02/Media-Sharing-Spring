package com.app.module.board.application.service;

import com.app.module.board.application.dto.request.BoardRequest;
import com.app.module.board.domain.Board;
import java.util.List;

public interface BoardService {
  Board save(BoardRequest boardRequest);

  Board addPinToBoard(Long pinId, Long boardId);

  Board deletePinFromBoard(Long pinId, Long boardId);

  Board update(Long id, String name);

  Board findById(Long id);

  List<Board> findAllByUserId(Long userId, int limit, int offset);

  void deleteIfUserMatches(Long id);
}
