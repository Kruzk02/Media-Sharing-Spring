package com.app.DAO.Impl;

import com.app.DAO.CommentDao;
import com.app.Model.Comment;
import com.app.Model.Hashtag;
import com.app.Model.SortType;
import com.app.exception.sub.CommentNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDaoImpl implements CommentDao {

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public CommentDaoImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Comment save(Comment comment) {
    try {
      String sql;
      if (comment.getMediaId() == 0) {
        sql = "INSERT INTO comments (content,user_id,pin_id) VALUES (?,?,?)";
      } else {
        sql = "INSERT INTO comments (content,user_id,pin_id, media_id) VALUES (?,?,?,?)";
      }
      KeyHolder keyHolder = new GeneratedKeyHolder();

      int row =
          jdbcTemplate.update(
              con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, comment.getContent());
                ps.setLong(2, comment.getUserId());
                ps.setLong(3, comment.getPinId());
                if (comment.getMediaId() != 0) {
                  ps.setLong(4, comment.getMediaId());
                }
                return ps;
              },
              keyHolder);

      if (row > 0) {
        comment.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        assignHashtagToComment(comment.getId(), comment.getHashtags());
        return comment;
      } else {
        return null;
      }
    } catch (DataAccessException e) {
      return null;
    }
  }

  private void assignHashtagToComment(Long commentId, Collection<Hashtag> hashtags) {
    String sql = "INSERT INTO hashtags_comments(hashtag_id, comment_id) VALUES(?, ?)";
    List<Hashtag> tags = hashtags.stream().toList();

    jdbcTemplate.batchUpdate(
        sql,
        new BatchPreparedStatementSetter() {

          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setLong(1, tags.get(i).getId());
            ps.setLong(2, commentId);
          }

          @Override
          public int getBatchSize() {
            return tags.size();
          }
        });
  }

  @Override
  public Comment update(Long id, Comment comment) {
    StringBuilder sb = new StringBuilder("UPDATE comments SET ");
    List<Object> params = new ArrayList<>();

    if (comment.getContent() != null) {
      sb.append("content = ?, ");
      params.add(comment.getContent());
    }

    if (comment.getMediaId() != 0) {
      sb.append("media_id = ?, ");
      params.add(comment.getMediaId());
    }

    if (comment.getHashtags() != null && !comment.getHashtags().isEmpty()) {
      String sql = "DELETE FROM hashtags_comments WHERe comment_id = ?";
      jdbcTemplate.update(sql, comment.getId());

      assignHashtagToComment(comment.getId(), comment.getHashtags());
    }

    if (params.isEmpty()) {
      throw new IllegalArgumentException("No fields to update");
    }

    if (!sb.isEmpty()) {
      sb.setLength(sb.length() - 2);
    }

    sb.append(" WHERE id = ?");
    params.add(id);

    String sql = sb.toString();
    int rowAffected = jdbcTemplate.update(sql, params.toArray());
    return rowAffected > 0 ? comment : null;
  }

  @Override
  public Comment findById(Long id, boolean fetchDetails) {
    try {
      if (fetchDetails) {
        String sql =
            "SELECT c.id AS comment_id, c.user_id, c.pin_id, c.created_at, "
                + "h.id AS hashtag_id, h.tag "
                + "FROM comments c "
                + "LEFT JOIN hashtags_comments hc ON hc.comment_id = c.id "
                + "LEFT JOIN hashtags h ON h.id = hc.hashtag_id "
                + "WHERE c.id = ?";
        return jdbcTemplate.query(sql, new CommentRSE(), id);
      } else {
        String sql = "SELECT id, user_id, pin_id, created_at FROM comments WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new CommentRowMapper(false, false, true), id);
      }
    } catch (DataAccessException e) {
      throw new CommentNotFoundException("Comment not found with a id: " + id);
    }
  }

  @Override
  public List<Comment> findByPinId(Long pinId, SortType sortType, int limit, int offset) {
    try {
      String sql =
          "SELECT id, content, user_id, media_id, created_at FROM comments WHERE pin_id = ? ORDER BY created_at "
              + sortType.getOrder()
              + " LIMIT ? OFFSET ?";
      return jdbcTemplate.query(sql, new CommentRowMapper(true, true, false), pinId, limit, offset);
    } catch (DataAccessException e) {
      System.out.println(e.getMessage());
      throw new CommentNotFoundException("Comment not found with a pin id: " + pinId);
    }
  }

  @Override
  public List<Comment> findByHashtag(String tag, int limit, int offset) {
    String sql =
        "SELECT c.id, c.user_id, c.pin_id, c.created_at "
            + "FROM comments c "
            + "JOIN hashtags_comments hc ON c.id = hc.comment_id "
            + "JOIN hashtags h ON hc.hashtag_id = h.id "
            + "WHERE h.tag = ? ORDER BY c.created_at DESC LIMIT ? OFFSET ?";
    return jdbcTemplate.query(sql, new CommentRowMapper(false, false, true), tag, limit, offset);
  }

  @Override
  public int deleteById(Long id) {
    try {
      String sql = "DELETE FROM comments WHERE id = ?";
      return jdbcTemplate.update(sql, id);
    } catch (DataAccessException e) {
      throw new CommentNotFoundException("Comment not found with a id: " + id);
    }
  }
}

class CommentRSE implements ResultSetExtractor<Comment> {

  @Override
  public Comment extractData(ResultSet rs) throws SQLException, DataAccessException {
    Comment comment = null;

    while (rs.next()) {
      if (comment == null) {
        comment =
            Comment.builder()
                .id(rs.getLong("comment_id"))
                .userId(rs.getLong("user_id"))
                .pinId(rs.getLong("pin_id"))
                .created_at(rs.getTimestamp("created_at").toLocalDateTime())
                .hashtags(new ArrayList<>())
                .build();
      }

      if (!rs.wasNull()) {
        Hashtag hashtag =
            Hashtag.builder().id(rs.getLong("hashtag_id")).tag(rs.getString("tag")).build();
        comment.getHashtags().add(hashtag);
      }
    }
    return comment;
  }
}

class CommentRowMapper implements RowMapper<Comment> {

  private final boolean includeContent;
  private final boolean includeMediaId;
  private final boolean includePinId;

  CommentRowMapper(boolean includeContent, boolean includeMediaId, boolean includePinId) {
    this.includeContent = includeContent;
    this.includeMediaId = includeMediaId;
    this.includePinId = includePinId;
  }

  @Override
  public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
    Comment comment = new Comment();
    comment.setId(rs.getLong("id"));
    if (includeContent) {
      comment.setContent(rs.getString("content"));
    }

    comment.setUserId(rs.getLong("user_id"));
    if (includePinId) {
      comment.setPinId(rs.getLong("pin_id"));
    }

    if (includeMediaId) {
      comment.setMediaId(rs.getLong("media_id"));
    }

    comment.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
    return comment;
  }
}
