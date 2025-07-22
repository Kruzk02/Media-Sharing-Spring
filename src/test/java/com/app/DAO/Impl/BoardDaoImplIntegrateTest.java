package com.app.DAO.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.DAO.AbstractMySQLTest;
import com.app.DAO.BoardDao;
import com.app.Model.Board;
import com.app.Model.Gender;
import com.app.Model.Media;
import com.app.Model.Pin;
import com.app.Model.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BoardDaoImplIntegrateTest extends AbstractMySQLTest {

  private BoardDao boardDao;

  @BeforeEach
  void setUp() {
    boardDao = new BoardDaoImpl(jdbcTemplate);
  }

  @Test
  @Order(1)
  void save() {
    Board result =
        boardDao.save(
            Board.builder()
                .name("name")
                .user(
                    User.builder()
                        .id(1L)
                        .username("username")
                        .email("email@gmail.com")
                        .password("HashedPassword")
                        .gender(Gender.MALE)
                        .bio("bio")
                        .enable(false)
                        .media(Media.builder().id(1L).build())
                        .build())
                .build());

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("name", result.getName());
  }

  @Test
  @Order(2)
  void addPinToBoard() {
    jdbcTemplate.update(
        "INSERT INTO pins(user_id, description, media_id) VALUES (?, ?, ?)", 1L, "description", 1L);
    Board result =
        boardDao.addPinToBoard(
            Pin.builder().id(1L).userId(1L).mediaId(1L).description("description").build(),
            Board.builder().id(1L).name("name").pins(new ArrayList<>()).build());

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("name", result.getName());
    assertIterableEquals(
        List.of(
            Pin.builder()
                .id(1L)
                .userId(1L)
                .mediaId(1L)
                .hashtags(null)
                .description("description")
                .build()),
        result.getPins());
  }

  @Test
  @Order(3)
  void deletePinFromBoard() {
    Board result =
        boardDao.deletePinFromBoard(
            Pin.builder().id(1L).userId(1L).mediaId(1L).description("description").build(),
            Board.builder().id(1L).name("name").pins(new ArrayList<>()).build());

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("name", result.getName());
    assertEquals(Collections.emptyList(), result.getPins());
  }

  @Test
  @Order(4)
  void findById() {
    Board result = boardDao.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("name", result.getName());
    assertEquals(Collections.emptyList(), result.getPins());
  }

  @Test
  @Order(5)
  void findAllByUserId() {
    List<Board> result = boardDao.findAllByUserId(1L, 10, 0);

    Board board = result.getFirst();

    assertNotNull(result);
    assertEquals(1L, board.getId());
    assertEquals("name", board.getName());
    assertEquals(Collections.emptyList(), board.getPins());
  }

  @Test
  @Order(6)
  void update() {
    Board result =
        boardDao.update(Board.builder().id(1L).name("name123").pins(new ArrayList<>()).build(), 1L);

    assertNotNull(result);
    assertEquals("name123", result.getName());
  }

  @Test
  @Order(7)
  void deleteById() {
    int result = boardDao.deleteById(1L);

    assertEquals(1L, result);
  }
}
