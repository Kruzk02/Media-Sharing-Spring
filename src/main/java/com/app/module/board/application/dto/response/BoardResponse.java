package com.app.module.board.application.dto.response;

import com.app.module.board.domain.Board;
import java.util.List;

public record BoardResponse(long id, String name, Long userId, List<Long> pinId) {
  public static BoardResponse fromEntity(Board board) {
    return new BoardResponse(board.getId(), board.getName(), board.getUserId(), board.getPins());
  }
}
