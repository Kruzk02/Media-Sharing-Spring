package com.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.module.board.application.dto.request.BoardRequest;
import com.app.module.board.application.service.BoardServiceImpl;
import com.app.module.board.domain.Board;
import com.app.module.board.domain.BoardNotFoundException;
import com.app.module.board.infrastructure.BoardDao;
import com.app.shared.dto.response.UserDTO;
import com.app.shared.gateway.PinGateway;
import com.app.shared.gateway.UserGateway;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class BoardServiceImplTest {

  @Mock private BoardDao boardDao;
  @Mock private PinGateway pinGateway;
  @Mock private UserGateway userGateway;

  @InjectMocks private BoardServiceImpl boardService;
  private Board board;

  @BeforeEach
  void setUp() {
    board = Board.builder().id(1L).userId(1L).name("name").pins(List.of(1L)).build();
  }

  @Test
  void save_shouldSaveBoardSuccessfully() {
    Authentication auth = Mockito.mock(Authentication.class);
    Mockito.when(auth.getName()).thenReturn("username");

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);

    Mockito.when(userGateway.getUserByUsername(Mockito.anyString()))
        .thenReturn(new UserDTO(1L, "qwe"));

    Mockito.when(boardDao.save(Mockito.argThat(b -> b.getName() != null)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var result = boardService.save(new BoardRequest(new Long[] {1L}, "name"));

    assertNotNull(result);
    assertEquals(board.getName(), result.getName());
  }

  @Test
  void findById_shouldReturnBoard() {
    Mockito.when(boardDao.findById(1L)).thenReturn(board);
    var result = boardService.findById(1L);

    assertNotNull(result);
    assertEquals(board, result);
  }

  @Test
  void findById_shouldThrowException_whenBoardNotFound() {
    Mockito.when(boardDao.findById(1L)).thenReturn(null);
    assertThrows(BoardNotFoundException.class, () -> boardService.findById(1L));
  }

  @Test
  void findAllByUserId_shouldReturnListOfBoard() {
    Mockito.when(boardDao.findAllByUserId(1L, 10, 0)).thenReturn(List.of(board));
    var result = boardService.findAllByUserId(1L, 10, 0);

    assertNotNull(result);
    assertEquals(List.of(board), result);
  }

  @Test
  void findAllByUserId_shouldReturnEmptyList() {
    Mockito.when(boardDao.findAllByUserId(1L, 10, 0)).thenReturn(Collections.emptyList());
    var result = boardService.findAllByUserId(1L, 10, 0);

    assertEquals(Collections.emptyList(), result);
  }
}
