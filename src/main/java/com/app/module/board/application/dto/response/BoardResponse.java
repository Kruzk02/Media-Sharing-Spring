package com.app.module.board.application.dto.response;

import com.app.module.board.domain.Board;
import com.app.shared.dto.response.PinDTO;
import java.util.List;

public record BoardResponse(long id, String name, Long userId, List<PinDTO> pinDTOs) {
  public static BoardResponse fromEntity(Board board) {
    return new BoardResponse(
        board.getId(),
        board.getName(),
        board.getUserId(),
        board.getPins().stream()
            .map(pin -> new PinDTO(pin.getId(), pin.getUserId(), pin.getUserId()))
            .toList());
  }
}
