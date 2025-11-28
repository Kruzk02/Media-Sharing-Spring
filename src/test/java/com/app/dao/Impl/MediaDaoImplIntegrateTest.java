package com.app.dao.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.dao.AbstractMySQLTest;
import com.app.module.media.dao.MediaDao;
import com.app.module.media.dao.MediaDaoImpl;
import com.app.module.media.domain.entity.Media;
import com.app.module.media.domain.status.MediaType;
import com.app.shared.type.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MediaDaoImplIntegrateTest extends AbstractMySQLTest {

  private MediaDao mediaDao;

  @BeforeEach
  void setUp() {
    mediaDao = new MediaDaoImpl(jdbcTemplate);
  }

  @Test
  @Order(1)
  void save() {
    var result =
        mediaDao.save(
            Media.builder().url("url").mediaType(MediaType.IMAGE).status(Status.PENDING).build());

    assertNotNull(result);
    assertEquals(2L, result.getId());
    assertEquals("url", result.getUrl());
  }

  @Test
  @Order(2)
  void update() {
    var saved =
        mediaDao.save(
            Media.builder().url("url").mediaType(MediaType.IMAGE).status(Status.PENDING).build());

    var result =
        mediaDao.update(
            saved.getId(),
            (Media.builder()
                .id(saved.getId())
                .url("url123")
                .mediaType(MediaType.IMAGE)
                .status(Status.READY)
                .build()));

    assertNotNull(result);
    assertEquals(saved.getId(), result.getId());
    assertEquals("url123", result.getUrl());
    assertEquals(Status.READY, result.getStatus());
  }

  @Test
  @Order(3)
  void updateStatus() {
    var saved =
        mediaDao.save(
            Media.builder().url("url").mediaType(MediaType.IMAGE).status(Status.PENDING).build());

    mediaDao.updateStatus(saved.getId(), Status.READY);
    var updated = mediaDao.findById(saved.getId());

    assertNotNull(saved);
    assertEquals(Status.READY, updated.getStatus());
  }

  @Test
  @Order(4)
  void findById() {
    var result = mediaDao.findById(2L);

    assertNotNull(result);
    assertEquals(2L, result.getId());
  }

  @Test
  @Order(5)
  void findByCommentId() {
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
  @Order(6)
  void deleteById() {
    var result = mediaDao.deleteById(1L);

    assertEquals(1, result);
  }
}
