package com.app.module.board.infrastructure;

import com.app.module.board.domain.Board;
import com.app.module.board.domain.BoardNotFoundException;
import com.app.module.pin.domain.Pin;
import com.app.module.user.domain.entity.User;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/** Implementation of BoardDao using Spring JDBC for data access. */
@Repository
@AllArgsConstructor
public class BoardDaoImpl implements BoardDao {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Board save(Board board) {
    String sql = "INSERT INTO boards (user_id, board_name) VALUES (?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();

    try {
      int row =
          jdbcTemplate.update(
              con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, board.getUser().getId());
                ps.setString(2, board.getName());
                return ps;
              },
              keyHolder);

      if (row > 0) {
        Number lastInsertedId = keyHolder.getKey();
        if (lastInsertedId == null) {
          throw new RuntimeException("Failed to retrieve generated board ID");
        }

        board.setId(keyHolder.getKey().longValue());

        if (board.getPins() != null && !board.getPins().isEmpty()) {
          String boardPinSql = "INSERT INTO board_pin (board_id, pin_id) VALUES (?, ?)";
          jdbcTemplate.batchUpdate(
              boardPinSql,
              board.getPins(),
              board.getPins().size(),
              (ps, pin) -> {
                ps.setLong(1, board.getId());
                ps.setLong(2, pin.getId());
              });
        }

        return board;
      } else {
        throw new RuntimeException("Failed to insert board");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error saving board", e);
    }
  }

  @Override
  public Board addPinToBoard(Pin pin, Board board) {
    String sql = "INSERT INTO board_pin (board_id, pin_id) VALUES(?,?)";
    int rowAffected = jdbcTemplate.update(sql, board.getId(), pin.getId());

    if (rowAffected > 0) {
      board.getPins().add(pin);
      return board;
    }
    return null;
  }

  @Override
  public Board deletePinFromBoard(Pin pin, Board board) {
    String sql = "DELETE FROM board_pin WHERE board_id = ? AND pin_id = ?";
    int rowAffected = jdbcTemplate.update(sql, board.getId(), pin.getId());
    if (rowAffected > 0) {
      board.getPins().remove(pin);
      return board;
    }
    return null;
  }

  @Override
  public Board update(Long id, Board board) {
    int rowAffected =
        jdbcTemplate.update("UPDATE boards SET board_name = ? WHERE id = ? ", board.getName(), id);
    return rowAffected > 0 ? board : null;
  }

  @Override
  public Board findById(Long id) {
    try {
      String boardSql =
          """
            SELECT b.id AS board_id, b.board_name,
                   u.id AS user_id, u.username
            FROM boards b
            JOIN users u ON b.user_id = u.id
            WHERE b.id = ?
        """;

      Board board =
          jdbcTemplate.queryForObject(
              boardSql,
              (rs, _) -> {
                var b =
                    Board.builder()
                        .id(rs.getLong("board_id"))
                        .name(rs.getString("board_name"))
                        .build();
                var user =
                    User.builder()
                        .id(rs.getLong("user_id"))
                        .username(rs.getString("username"))
                        .build();
                b.setUser(user);
                return b;
              },
              id);

      if (board == null) {
        throw new BoardNotFoundException("Board not found with a id: " + id);
      }

      String pinsSql =
          """
            SELECT p.id AS pin_id, p.media_id, p.user_id AS pin_user_id, p.created_at AS pin_created_at
            FROM pins p
            JOIN board_pin bp ON bp.pin_id = p.id
            WHERE bp.board_id = ?
        """;

      List<Pin> pins =
          jdbcTemplate.query(
              pinsSql,
              (rs, _) ->
                  Pin.builder()
                      .id(rs.getLong("pin_id"))
                      .mediaId(rs.getLong("media_id"))
                      .userId(rs.getLong("pin_user_id"))
                      .createdAt(rs.getTimestamp("pin_created_at").toLocalDateTime())
                      .build(),
              id);

      board.setPins(pins);
      return board;
    } catch (DataAccessException e) {
      throw new RuntimeException("Database error in findById(" + id + "): " + e.getMessage(), e);
    }
  }

  @Override
  public List<Board> findAllByUserId(Long userId, int limit, int offset) {
    String boardSql =
        """
        SELECT b.id AS board_id, b.board_name,
            u.id AS user_id, u.username
        FROM boards b
        JOIN users u ON b.user_id = u.id
        WHERE b.user_id = ?
        ORDER BY b.id DESC
        LIMIT ? OFFSET ?
        """;
    var boards =
        jdbcTemplate.query(
            boardSql,
            (rs, _) -> {
              Board board = new Board();
              board.setId(rs.getLong("board_id"));
              board.setName(rs.getString("board_name"));

              User user = new User();
              user.setId(rs.getLong("user_id"));
              user.setUsername(rs.getString("username"));
              board.setUser(user);

              board.setPins(new ArrayList<>());
              return board;
            },
            userId,
            limit,
            offset);

    if (boards.isEmpty()) return boards;

    List<Long> boardsIds = boards.stream().map(Board::getId).toList();

    String inSql = boardsIds.stream().map(id -> "?").collect(Collectors.joining(", "));

    String pinSql =
        String.format(
            """
      SELECT bp.board_id, p.id AS pin_id, p.media_id,
             p.user_id AS pin_user_id, p.created_at AS pin_created_at
      FROM board_pin bp
      JOIN pins p ON p.id = bp.pin_id
      WHERE bp.board_id IN (%s)
      """,
            inSql);

    Map<Long, List<Pin>> pinMap = new HashMap<>();

    jdbcTemplate.query(
        pinSql,
        rs -> {
          long boardId = rs.getLong("board_id");
          Pin pin = new Pin();
          pin.setId(rs.getLong("pin_id"));
          pin.setMediaId(rs.getLong("media_id"));
          pin.setUserId(rs.getLong("pin_user_id"));
          pin.setCreatedAt(rs.getTimestamp("pin_created_at").toLocalDateTime());
          pinMap.computeIfAbsent(boardId, k -> new ArrayList<>()).add(pin);
        },
        boardsIds.toArray());

    for (Board board : boards) {
      var pins = pinMap.get(board.getId());
      if (pins != null) {
        board.getPins().addAll(pins);
      }
    }
    return boards;
  }

  @Override
  public int deleteById(Long id) {
    try {
      String sql = "DELETE FROM boards WHERE id = ?";
      return jdbcTemplate.update(sql, id);
    } catch (EmptyResultDataAccessException e) {
      throw new BoardNotFoundException("Board not found with a id: " + id);
    }
  }
}
