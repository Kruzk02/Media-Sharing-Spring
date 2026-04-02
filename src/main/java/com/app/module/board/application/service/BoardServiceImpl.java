package com.app.module.board.application.service;

import com.app.module.board.application.dto.request.BoardRequest;
import com.app.module.board.application.exception.NameValidationException;
import com.app.module.board.application.exception.PinNotInBoardException;
import com.app.module.board.domain.Board;
import com.app.module.board.domain.BoardNotFoundException;
import com.app.module.board.infrastructure.BoardDao;
import com.app.shared.dto.response.PinDTO;
import com.app.shared.dto.response.UserDTO;
import com.app.shared.exception.sub.*;
import com.app.shared.exception.sub.UserNotMatchException;
import com.app.shared.gateway.PinGateway;
import com.app.shared.gateway.UserGateway;
import java.util.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Board Service class responsible for handling operations related to boards.
 *
 * <p>This class interacts with the BoardDaoImpl and PinDaoImpl for data access, and utilizes
 * ModelMapper for mapping between DTOs and entity objects.
 */
@Service
@Qualifier("boardServiceImpl")
@AllArgsConstructor
public class BoardServiceImpl implements BoardService {

  private final BoardDao boardDao;
  private final PinGateway pinGateway;
  private final UserGateway userGateway;

  private UserDTO getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userGateway.getUserByUsername(Objects.requireNonNull(authentication).getName());
  }

  /**
   * Saves a new board based on the provided boardDTO.
   *
   * <p>Retrieves the corresponding pin from the pinDao, maps the boardDTO to a Board entity.
   *
   * <p>Sets the retrieved pin to the board, and saves the board using boardDao.
   *
   * @param boardRequest the boardRequest object containing board information.
   * @return The saved Board entity.
   */
  @Override
  public Board save(BoardRequest boardRequest) {

    if (boardRequest.name() == null) {
      throw new NameValidationException("Board name shouldn't be empty");
    }

    if (boardRequest.name().length() <= 3 || boardRequest.name().length() >= 256) {
      throw new NameValidationException(
          "Board name should be longer than 3 characters and less than 256 characters");
    }

    Board board =
        Board.builder().name(boardRequest.name()).userId(getAuthenticatedUser().id()).build();

    if (boardRequest.pin_id() != null && boardRequest.pin_id().length > 0) {
      List<PinDTO> pinDTOs = pinGateway.getPinsByIds(Arrays.asList(boardRequest.pin_id()));
      if (!pinDTOs.isEmpty()) {
        for (var pinDto : pinDTOs) {
          board.getPins().add(pinDto.id());
        }
      }
    } else {
      board.setPins(Collections.emptyList());
    }

    return boardDao.save(board);
  }

  @Override
  public Board addPinToBoard(Long pinId, Long boardId) {
    PinDTO pinDTO = pinGateway.getPinById(pinId);
    if (pinDTO == null) {
      throw new PinNotFoundException("Pin not found with ID: " + pinId);
    }

    Board board = boardDao.findById(boardId);
    if (board == null) {
      throw new BoardNotFoundException("Board not found with ID: " + boardId);
    }

    if (!board.getUserId().equals(getAuthenticatedUser().id())) {
      throw new UserNotMatchException("Authenticated user does not own this board");
    }

    if (board.getPins().contains(pinDTO.id())) {
      throw new PinAlreadyExistingException("Pin already exists in the board");
    }

    return boardDao.addPinToBoard(pinDTO.id(), board);
  }

  @Override
  public Board deletePinFromBoard(Long pinId, Long boardId) {
    PinDTO pinDTO = pinGateway.getPinById(pinId);
    if (pinDTO == null) {
      throw new PinNotFoundException("Pin not found with ID: " + pinId);
    }

    Board board = boardDao.findById(boardId);
    if (board == null) {
      throw new BoardNotFoundException("Board not found with ID: " + boardId);
    }

    if (!board.getUserId().equals(getAuthenticatedUser().id())) {
      throw new UserNotMatchException("Authenticated user does not own this board");
    }

    if (board.getPins().stream().noneMatch(pinDTO.id()::equals)) {
      throw new PinNotInBoardException("Pin not found in a board");
    }

    return boardDao.deletePinFromBoard(pinDTO.id(), board);
  }

  @Override
  public Board update(Long id, String name) {
    Board existingBoard = boardDao.findById(id);
    if (existingBoard == null) {
      throw new BoardNotFoundException("Board not found with a id: " + id);
    }

    if (!Objects.equals(existingBoard.getUserId(), getAuthenticatedUser().id())) {
      throw new UserNotMatchException("Authenticated user not own this board");
    }

    existingBoard.setName(name != null ? name : existingBoard.getName());

    return boardDao.update(id, existingBoard);
  }

  /**
   * Retrieves a board by its ID.
   *
   * @param id The ID of the board to retrieve.
   * @return The Board entity corresponding to the provided ID.
   */
  @Override
  public Board findById(Long id) {
    Board board = boardDao.findById(id);
    if (board == null) {
      throw new BoardNotFoundException("Board not found with a id: " + id);
    }
    return board;
  }

  @Override
  public List<Board> findAllByUserId(Long userId, int limit, int offset) {
    List<Board> boards = boardDao.findAllByUserId(userId, limit, offset);
    if (boards.isEmpty()) {
      return Collections.emptyList();
    }

    return boards;
  }

  /**
   * Deletes a board by its ID if user id match with board.
   *
   * @param id The ID of the board to delete.
   */
  @Override
  public void deleteIfUserMatches(Long id) {
    Board board =
        Optional.ofNullable(boardDao.findById(id))
            .orElseThrow(() -> new BoardNotFoundException("Board not found with a id"));

    if (!Objects.equals(board.getUserId(), getAuthenticatedUser().id())) {
      throw new UserNotMatchException("Authenticated user does not own this board");
    }

    boardDao.deleteById(id);
  }
}
