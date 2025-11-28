package com.app.dao.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.dao.AbstractMySQLTest;
import com.app.module.hashtag.domain.Hashtag;
import com.app.module.pin.domain.Pin;
import com.app.module.pin.infrastructure.PinDao;
import com.app.module.pin.infrastructure.PinDaoImpl;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PinDaoImplIntegrateTest extends AbstractMySQLTest {

  private PinDao pinDao;

  @BeforeEach
  void setUp() {
    pinDao = new PinDaoImpl(jdbcTemplate);
  }

  @Test
  @Order(1)
  void save() {
    jdbcTemplate.update("INSERT INTO hashtags(tag) VALUES(?)", "tag");

    Pin result =
        pinDao.save(
            Pin.builder()
                .userId(1L)
                .mediaId(1L)
                .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
                .description("description")
                .build());

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(1L, result.getUserId());
    assertEquals(1L, result.getMediaId());
  }

  @Test
  @Order(2)
  void getAllPins() {
    List<Pin> pins = pinDao.getAllPins(SortType.NEWEST, 10, 0);

    Pin expected = Pin.builder().id(1L).userId(1L).mediaId(1L).description(null).build();

    Pin actual = pins.getFirst();

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getUserId(), actual.getUserId());
    assertEquals(expected.getMediaId(), actual.getMediaId());
    assertEquals(expected.getDescription(), actual.getDescription());
    assertNotNull(actual.getCreatedAt());
  }

  @Test
  @Order(3)
  void getAllPinsByHashtag() {
    List<Pin> pins = pinDao.getAllPinsByHashtag("tag", 10, 0);
    Pin expected =
        Pin.builder()
            .id(1L)
            .userId(1L)
            .mediaId(1L)
            .description(null)
            .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
            .build();

    Pin actual = pins.getFirst();

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getUserId(), actual.getUserId());
    assertEquals(expected.getMediaId(), actual.getMediaId());
    assertEquals(expected.getDescription(), actual.getDescription());
    assertNotNull(actual.getCreatedAt());
  }

  @Test
  @Order(4)
  void findById() {
    Pin result = pinDao.findById(1L, DetailsType.BASIC);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(1L, result.getUserId());
    assertEquals(1L, result.getMediaId());
  }

  @Test
  @Order(5)
  void findPinByUserId() {
    List<Pin> result = pinDao.findPinByUserId(1L, 10, 0);
    Pin expected =
        Pin.builder()
            .id(1L)
            .userId(1L)
            .mediaId(1L)
            .description(null)
            .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
            .build();

    Pin actual = result.getFirst();

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getUserId(), actual.getUserId());
    assertEquals(expected.getMediaId(), actual.getMediaId());
    assertEquals(expected.getDescription(), actual.getDescription());
    assertNotNull(actual.getCreatedAt());
  }

  @Test
  @Order(6)
  void update() {
    Pin pin =
        pinDao.update(
            1L,
            Pin.builder()
                .id(1L)
                .userId(1L)
                .mediaId(1L)
                .description("Hello World")
                .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
                .build());

    assertNotNull(pin);
    assertEquals(1L, pin.getId());
    assertEquals(1L, pin.getUserId());
    assertEquals(1L, pin.getMediaId());
    assertEquals("Hello World", pin.getDescription());
  }

  @Test
  @Order(7)
  void deleteById() {
    int result = pinDao.deleteById(1L);

    assertEquals(1, result);
  }
}
