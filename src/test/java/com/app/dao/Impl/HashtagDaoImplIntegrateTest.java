package com.app.dao.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.dao.AbstractMySQLTest;
import com.app.module.hashtag.domain.Hashtag;
import com.app.module.hashtag.infrastructure.HashtagDao;
import com.app.module.hashtag.infrastructure.HashtagDaoImpl;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HashtagDaoImplIntegrateTest extends AbstractMySQLTest {

  private HashtagDao hashtagDao;

  @BeforeEach
  void setUp() {
    hashtagDao = new HashtagDaoImpl(jdbcTemplate);
  }

  @Test
  @Order(1)
  void save() {
    Hashtag result = hashtagDao.save(Hashtag.builder().tag("tag").build());

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  @Order(2)
  void findByTag() {
    Map<String, Hashtag> result = hashtagDao.findByTag(Set.of("tag"));

    assertEquals(1, result.size());

    assertTrue(result.containsKey("tag"));
  }
}
