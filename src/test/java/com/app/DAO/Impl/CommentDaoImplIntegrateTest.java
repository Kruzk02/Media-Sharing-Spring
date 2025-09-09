package com.app.DAO.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.DAO.AbstractMySQLTest;
import com.app.DAO.CommentDao;
import com.app.Model.Comment;
import com.app.Model.DetailsType;
import com.app.Model.Hashtag;
import com.app.Model.SortType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentDaoImplIntegrateTest extends AbstractMySQLTest {

  private CommentDao commentDao;

  @BeforeEach
  void setUp() {
    commentDao = new CommentDaoImpl(jdbcTemplate);
  }

  @Test
  @Order(1)
  void save() {
    jdbcTemplate.update("INSERT INTO hashtags(tag) VALUES(?)", "tag");
    jdbcTemplate.update(
        "INSERT INTO pins(user_id, description, media_id) VALUES (?, ?, ?)", 1L, "description", 1L);

    Comment result =
        commentDao.save(
            Comment.builder()
                .content("content")
                .pinId(1L)
                .userId(1L)
                .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
                .mediaId(1L)
                .build());

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("content", result.getContent());
    assertEquals(1L, result.getPinId());
    assertEquals(1L, result.getUserId());
  }

  @Test
  @Order(2)
  void findById() {
    Comment result = commentDao.findById(1L, DetailsType.DETAIL);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(1L, result.getPinId());
    assertEquals(1L, result.getUserId());
    assertIterableEquals(
        List.of(Hashtag.builder().id(1L).tag("tag").build()), result.getHashtags());
  }

  @Test
  @Order(3)
  void findByPinId() {
    List<Comment> result = commentDao.findByPinId(1L, SortType.NEWEST, 10, 0);

    Comment comment = result.getFirst();

    assertNotNull(result);
    assertEquals(1L, comment.getId());
    assertEquals("content", comment.getContent());
    assertEquals(1L, comment.getUserId());
  }

  @Test
  @Order(4)
  void findByHashtag() {
    List<Comment> result = commentDao.findByHashtag("tag", 10, 0);

    Comment comment = result.getFirst();

    assertNotNull(result);
    assertEquals(1L, comment.getId());
    assertEquals(1L, comment.getPinId());
    assertEquals(1L, comment.getUserId());
  }

  @Test
  @Order(5)
  void update() {
    Comment result =
        commentDao.update(
            1L,
            Comment.builder()
                .id(1L)
                .content("content123")
                .pinId(1L)
                .userId(1L)
                .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
                .mediaId(1L)
                .build());

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(1L, result.getPinId());
    assertEquals(1L, result.getUserId());
    assertIterableEquals(
        List.of(Hashtag.builder().id(1L).tag("tag").build()), result.getHashtags());
  }

  @Test
  @Order(6)
  void deleteById() {
    int result = commentDao.deleteById(1L);

    assertEquals(1L, result);
  }
}
