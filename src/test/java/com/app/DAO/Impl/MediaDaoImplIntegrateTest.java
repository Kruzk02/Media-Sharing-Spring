package com.app.DAO.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.DAO.AbstractMySQLTest;
import com.app.DAO.MediaDao;
import com.app.Model.Media;
import com.app.Model.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

class MediaDaoImplIntegrateTest extends AbstractMySQLTest {

  private MediaDao mediaDao;

  @BeforeEach
  void setUp() {
    mediaDao = new MediaDaoImpl(jdbcTemplate);
  }

  @Test
  void save() {
    var result = mediaDao.save(Media.builder().url("url").mediaType(MediaType.IMAGE).build());

    assertNotNull(result);
    assertEquals(3L, result.getId());
    assertEquals("url", result.getUrl());
  }

  @Test
  void update() {
    var saved = mediaDao.save(Media.builder().url("url").mediaType(MediaType.IMAGE).build());

    var result =
        mediaDao.update(
            saved.getId(),
            (Media.builder().id(saved.getId()).url("url123").mediaType(MediaType.IMAGE).build()));

    assertNotNull(result);
    assertEquals(saved.getId(), result.getId());
    assertEquals("url123", result.getUrl());
  }

  @Test
  void findById() {
    var result = mediaDao.findById(2L);

    assertNotNull(result);
    assertEquals(2L, result.getId());
  }

  @Test
  void findByCommentId() {
    jdbcTemplate.update(
        "INSERT INTO users (id, username, email, password, gender, bio, enable, media_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
        1,
        "username",
        "email@gmail.com",
        "HashedPassword",
        "male",
        "bio",
        false,
        1);
    jdbcTemplate.update(
        "INSERT INTO pins(user_id, description, media_id) VALUES (?, ?, ?)", 1L, "description", 1L);
    jdbcTemplate.update(
        "INSERT INTO comments (content,user_id,pin_id, media_id) VALUES (?,?,?,?)",
        "content",
        1L,
        1L,
        1L);
    var result = mediaDao.findByCommentId(1L);

    assertNotNull(result);
  }

  @Test
  @Order(5)
  void deleteById() {
    var result = mediaDao.deleteById(1L);

    assertEquals(1, result);
  }
}
