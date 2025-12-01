package com.app.service.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.app.module.board.application.service.BoardService;
import com.app.module.board.application.service.CachedBoardService;
import com.app.module.board.domain.Board;
import com.app.module.user.domain.entity.User;
import com.app.module.user.domain.status.Gender;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CachedBoardServiceTest extends AbstractRedisTest<Board> {

  private CachedBoardService cachedBoardService;
  private BoardService boardService;

  private Board board;

  @BeforeEach
  @Override
  void setUp() {
    super.setUp();

    boardService = mock(BoardService.class);
    cachedBoardService = new CachedBoardService(redisTemplate, boardService);

    board =
        Board.builder()
            .id(3L)
            .name("name")
            .user(
                User.builder()
                    .id(1L)
                    .username("username")
                    .email("email@gmail.com")
                    .password("HashedPassword")
                    .gender(Gender.MALE)
                    .mediaId(1L)
                    .bio("bio")
                    .enable(false)
                    .build())
            .build();
  }

  @Test
  void findAllByUserId() {
    List<Board> boards = boardService.findAllByUserId(1L, 10, 0);
    assertTrue(boards.isEmpty());
  }

  @Test
  void addPinToBoard() {
    when(boardService.addPinToBoard(1L, 1L)).thenReturn(board);

    var b = cachedBoardService.addPinToBoard(1L, 1L);

    assertNotNull(b);
  }

  @Test
  void deletePinFromBoard() {
    when(boardService.addPinToBoard(1L, 1L)).thenReturn(board);

    var b = cachedBoardService.deletePinFromBoard(1L, 1L);

    assertNull(b);
  }
}
