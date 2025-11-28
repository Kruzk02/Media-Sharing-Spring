package com.app.module.follower.infrastructure;

import com.app.module.follower.domain.Follower;
import com.app.module.media.domain.entity.Media;
import com.app.module.user.domain.entity.User;
import com.app.module.user.domain.status.Gender;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class FollowerDaoImpl implements FollowerDao {

  private final JdbcTemplate template;

  @Override
  public List<User> getAllFollowingByUserId(long userId, int limit) {
    try {
      String sql =
          "SELECT u.id AS user_id, u.username, u.email, u.media_id, u.bio, u.gender "
              + "FROM followers f "
              + "JOIN users u ON f.following_id = u.id "
              + "WHERE f.follower_id = ? "
              + "ORDER BY u.id ASC LIMIT ?";
      return template.query(
          sql,
          (rs, rowNum) ->
              User.builder()
                  .id(rs.getLong("user_id"))
                  .username(rs.getString("username"))
                  .email(rs.getString("email"))
                  .bio(rs.getString("bio"))
                  .gender(Gender.valueOf(rs.getString("gender").toUpperCase()))
                  .media(Media.builder().id(rs.getLong("media_id")).build())
                  .build(),
          userId,
          limit);
    } catch (DataAccessException e) {
      throw new RuntimeException("Error fetching followers: " + e.getMessage(), e);
    }
  }

  @Override
  public boolean isFollowing(long authUserId, long targetId) {
    try {
      String sql = "SELECT COUNT(*) FROM followers WHERE follower_id = ? AND following_id = ?";
      Integer count = template.queryForObject(sql, Integer.class, authUserId, targetId);
      return count > 0;
    } catch (DataAccessException e) {
      throw new RuntimeException("Error checking follow status: " + e.getMessage(), e);
    }
  }

  @Override
  public Follower followUser(long authUserId, long targetId) {
    try {
      String sql = "INSERT INTO followers (follower_id, following_id) VALUES (?, ?)";
      int rowsAffected = template.update(sql, authUserId, targetId);
      if (rowsAffected > 0) {
        return Follower.builder().followerId(authUserId).followingId(targetId).build();
      }
      return null;
    } catch (DataAccessException e) {
      throw new RuntimeException("Error adding follower: " + e.getMessage(), e);
    }
  }

  @Override
  public int unfollowUser(long authUserId, long targetId) {
    try {
      String sql = "DELETE FROM followers WHERE follower_id = ? AND following_id = ?";
      return template.update(sql, authUserId, targetId);
    } catch (DataAccessException e) {
      throw new RuntimeException("Error removing follower: " + e.getMessage(), e);
    }
  }
}
