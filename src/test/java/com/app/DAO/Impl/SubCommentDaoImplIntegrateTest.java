package com.app.DAO.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.DAO.AbstractMySQLTest;
import com.app.DAO.SubCommentDao;
import com.app.Model.Comment;
import com.app.Model.Gender;
import com.app.Model.Hashtag;
import com.app.Model.Media;
import com.app.Model.MediaType;
import com.app.Model.SortType;
import com.app.Model.SubComment;
import com.app.Model.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SubCommentDaoImplIntegrateTest extends AbstractMySQLTest {

  private SubCommentDao subCommentDao;

  @BeforeEach
  void setUp() {
    subCommentDao = new SubCommentDaoImpl(jdbcTemplate);
  }

  @Test
  @Order(1)
  void save() {
    jdbcTemplate.update(
        "INSERT INTO pins(user_id, description, media_id) VALUES (?, ?, ?)", 1L, "description", 1L);
    jdbcTemplate.update(
        "INSERT INTO comments (content,user_id,pin_id, media_id) VALUES (?,?,?,?)",
        "content",
        1L,
        1L,
        1L);
    SubComment result =
        subCommentDao.save(
            SubComment.builder()
                .content("content")
                .user(
                    User.builder()
                        .id(1L)
                        .username("username")
                        .email("email@gmail.com")
                        .password("HashedPassword")
                        .gender(Gender.MALE)
                        .media(Media.builder().id(1L).mediaType(MediaType.IMAGE).url("url").build())
                        .bio("bio")
                        .enable(false)
                        .build())
                .comment(
                    Comment.builder()
                        .id(1L)
                        .content("content123")
                        .pinId(1L)
                        .userId(1L)
                        .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
                        .mediaId(1L)
                        .build())
                .media(Media.builder().id(1L).url("url").mediaType(MediaType.IMAGE).build())
                .build());

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  @Order(2)
  void findAllByCommentId() {
    List<SubComment> result = subCommentDao.findAllByCommentId(1L, SortType.NEWEST, 10, 0);
    SubComment subComment = result.getFirst();

    assertNotNull(subComment);
    assertEquals(1L, subComment.getId());
    assertEquals("content", subComment.getContent());
  }

  @Test
  @Order(3)
  void findById() {
    SubComment result = subCommentDao.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("content", result.getContent());
  }

  @Test
  @Order(4)
  void update() {
    SubComment result =
        subCommentDao.update(
            1L,
            SubComment.builder()
                .id(1L)
                .content("content123")
                .user(
                    User.builder()
                        .id(1L)
                        .username("username")
                        .email("email@gmail.com")
                        .password("HashedPassword")
                        .gender(Gender.MALE)
                        .media(Media.builder().id(1L).mediaType(MediaType.IMAGE).url("url").build())
                        .bio("bio")
                        .enable(false)
                        .build())
                .comment(
                    Comment.builder()
                        .id(1L)
                        .content("content123")
                        .pinId(1L)
                        .userId(1L)
                        .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
                        .mediaId(1L)
                        .build())
                .media(Media.builder().id(1L).url("url").mediaType(MediaType.IMAGE).build())
                .build());

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("content123", result.getContent());
  }

  @Test
  @Order(5)
  void deleteById() {
    int result = subCommentDao.deleteById(1L);

    assertEquals(1L, result);
  }
}
