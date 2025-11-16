package com.app.dto.response;

import com.app.model.Board;
import java.util.List;

public record BoardResponse(long id, String name, UserDTO userDTO, List<PinDTO> pinDTOs) {
  public static BoardResponse fromEntity(Board board) {
    return new BoardResponse(
        board.getId(),
        board.getName(),
        new UserDTO(board.getUser().getId(), board.getUser().getUsername()),
        board.getPins().stream()
            .map(pin -> new PinDTO(pin.getId(), pin.getUserId(), pin.getUserId()))
            .toList());
  }
}
