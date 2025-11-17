package com.app.dao.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.dao.AbstractMySQLTest;
import com.app.module.follower.dao.FollowerDao;
import com.app.module.follower.dao.FollowerDaoImpl;
import com.app.module.follower.model.Follower;
import com.app.module.media.model.Media;
import com.app.module.user.model.Gender;
import com.app.module.user.model.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerDaoImplIntegrateTest extends AbstractMySQLTest {

  private FollowerDao followerDao;

  @BeforeEach
  void setUp() {
    followerDao = new FollowerDaoImpl(jdbcTemplate);
  }

  @Test
  @Order(1)
  void followUser() {
    Follower result = followerDao.followUser(1L, 2L);

    assertNotNull(result);
    assertEquals(1L, result.getFollowerId());
    assertEquals(2L, result.getFollowingId());
  }

  @Test
  @Order(2)
  void isFollowing() {
    boolean result = followerDao.isFollowing(1L, 2L);

    assertTrue(result);
  }

  @Test
  @Order(3)
  void getAllFollowingByUserId() {
    List<User> result = followerDao.getAllFollowingByUserId(1L, 10);

    assertNotNull(result);
    assertIterableEquals(
        List.of(
            User.builder()
                .id(2L)
                .username("username2")
                .email("email2@gmail.com")
                .roles(null)
                .media(Media.builder().id(1L).url(null).mediaType(null).build())
                .bio("bio")
                .gender(Gender.MALE)
                .enable(null)
                .build()),
        result);
  }

  @Test
  @Order(4)
  void unfollowUser() {
    int result = followerDao.unfollowUser(1L, 2L);

    assertEquals(1, result);
  }
}
