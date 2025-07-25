package com.app.DAO.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.Model.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
class FollowerDaoImplTest {

  @InjectMocks private FollowerDaoImpl followerDao;
  @Mock private JdbcTemplate jdbcTemplate;

  private User user;

  @BeforeEach
  void setUp() {

    user =
        User.builder()
            .id(1L)
            .username("username")
            .email("email@gmail.com")
            .password("HashedPassword")
            .gender(Gender.MALE)
            .media(Media.builder().id(1L).mediaType(MediaType.IMAGE).url("NO").build())
            .roles(
                List.of(
                    Role.builder()
                        .id(2L)
                        .name("ROLE_USER")
                        .privileges(List.of(Privilege.builder().id(2L).name("READ").build()))
                        .build()))
            .bio("bio")
            .enable(false)
            .build();
  }

  @Test
  void getAllFollowingByUserId_shouldReturnListOfFollowing_whenFollowerExists() {
    Mockito.when(
            jdbcTemplate.query(
                Mockito.eq(
                    "SELECT u.id AS user_id, u.username, u.email, u.media_id, u.bio, u.gender "
                        + "FROM followers f "
                        + "JOIN users u ON f.following_id = u.id "
                        + "WHERE f.follower_id = ? "
                        + "ORDER BY u.id ASC LIMIT ?"),
                Mockito.any(RowMapper.class),
                Mockito.eq(1L),
                Mockito.eq(10)))
        .thenReturn(List.of(user));

    var result = followerDao.getAllFollowingByUserId(1L, 10);
    assertNotNull(result);
    assertEquals(List.of(user), result);

    Mockito.verify(jdbcTemplate)
        .query(
            Mockito.eq(
                "SELECT u.id AS user_id, u.username, u.email, u.media_id, u.bio, u.gender "
                    + "FROM followers f "
                    + "JOIN users u ON f.following_id = u.id "
                    + "WHERE f.follower_id = ? "
                    + "ORDER BY u.id ASC LIMIT ?"),
            Mockito.any(RowMapper.class),
            Mockito.eq(1L),
            Mockito.eq(10));
  }

  @Test
  void getAllFollowingByUserId_shouldThrowException_whenFollowerDoesNotExists() {
    Mockito.when(
            jdbcTemplate.query(
                Mockito.anyString(), Mockito.any(RowMapper.class), Mockito.eq(1L), Mockito.eq(19)))
        .thenThrow(new EmptyResultDataAccessException(1));

    assertThrows(RuntimeException.class, () -> followerDao.getAllFollowingByUserId(1L, 10));
  }

  @Test
  void isUserAlreadyFollowing_shouldReturnTrue_whenFollowingOther() {
    Mockito.when(
            jdbcTemplate.queryForObject(
                Mockito.eq(
                    "SELECT COUNT(*) FROM followers WHERE follower_id = ? AND following_id = ?"),
                Mockito.eq(Integer.class),
                Mockito.eq(1L),
                Mockito.eq(2L)))
        .thenReturn(1);

    var result = followerDao.isFollowing(1L, 2L);
    assertTrue(result);

    Mockito.verify(jdbcTemplate)
        .queryForObject(
            Mockito.eq("SELECT COUNT(*) FROM followers WHERE follower_id = ? AND following_id = ?"),
            Mockito.eq(Integer.class),
            Mockito.eq(1L),
            Mockito.eq(2L));
  }

  @Test
  void isUserAlreadyFollowing_shouldReturnFalse_whenUserNotFollowingOther() {
    Mockito.when(
            jdbcTemplate.queryForObject(
                Mockito.eq(
                    "SELECT COUNT(*) FROM followers WHERE follower_id = ? AND following_id = ?"),
                Mockito.eq(Integer.class),
                Mockito.eq(1L),
                Mockito.eq(2L)))
        .thenReturn(0);

    var result = followerDao.isFollowing(1L, 2L);
    assertFalse(result);

    Mockito.verify(jdbcTemplate)
        .queryForObject(
            Mockito.eq("SELECT COUNT(*) FROM followers WHERE follower_id = ? AND following_id = ?"),
            Mockito.eq(Integer.class),
            Mockito.eq(1L),
            Mockito.eq(2L));
  }

  @Test
  void followUser_shouldReturnFollower() {
    Mockito.when(
            jdbcTemplate.update(
                "INSERT INTO followers (follower_id, following_id) VALUES (?, ?)", 1L, 2L))
        .thenReturn(1);

    Follower result = followerDao.followUser(1L, 2L);

    assertNotNull(result);
    assertEquals(1L, result.getFollowerId());
    assertEquals(2L, result.getFollowingId());
  }

  @Test
  void followUser_shouldFail() {
    Mockito.when(
            jdbcTemplate.update(
                "INSERT INTO followers (follower_id, following_id) VALUES (?, ?)", 1L, 2L))
        .thenReturn(0);

    Follower result = followerDao.followUser(1L, 2L);

    assertNull(result);
  }

  @Test
  void testUnfollowUser_success() {
    Mockito.when(
            jdbcTemplate.update(
                "DELETE FROM followers WHERE follower_id = ? AND following_id = ?", 1L, 2L))
        .thenReturn(1);

    int result = followerDao.unfollowUser(1L, 2L);

    assertEquals(1, result);
  }

  @Test
  void testUnfollowUser_exception() {
    Mockito.when(
            jdbcTemplate.update(
                "DELETE FROM followers WHERE follower_id = ? AND following_id = ?", 1L, 2L))
        .thenThrow(new DataAccessException("DB error") {});

    assertThrows(RuntimeException.class, () -> followerDao.unfollowUser(1L, 2L));
  }
}
