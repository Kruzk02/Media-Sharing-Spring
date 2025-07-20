package com.app.DAO.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.DAO.AbstractMySQLTest;
import com.app.DAO.NotificationDao;
import com.app.Model.Notification;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class NotificationDaoImplIntegrateTest extends AbstractMySQLTest {

  private NotificationDao notificationDao;

  @BeforeEach
  void setUp() {
    notificationDao = new NotificationDaoImpl(jdbcTemplate);
  }

  @Test
  @Order(1)
  void save() {
    var keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(
        conn -> {
          var ps =
              conn.prepareStatement(
                  "INSERT INTO users (id, username, email, password) VALUES (?, ?, ?, ?)",
                  Statement.RETURN_GENERATED_KEYS);
          ps.setLong(1, 1L);
          ps.setString(2, "username");
          ps.setString(3, "email@gmail.com");
          ps.setString(4, "password");
          return ps;
        },
        keyHolder);

    var result =
        notificationDao.save(
            Notification.builder()
                .userId(keyHolder.getKey().longValue())
                .message("message")
                .build());

    assertNotNull(result);
    assertEquals(keyHolder.getKey().longValue(), result.getUserId());
    assertEquals("message", result.getMessage());
  }

  @Test
  @Order(2)
  void findByUserId() {
    insert();
    var result = notificationDao.findByUserId(1L, 10, 0, true);
    System.out.println(result);
    assertNotNull(result);
  }

  void insert() {
    jdbcTemplate.update(
        conn -> {
          var ps =
              conn.prepareStatement(
                  "INSERT INTO users (id, username, email, password) VALUES (?, ?, ?, ?)",
                  Statement.RETURN_GENERATED_KEYS);
          ps.setLong(1, 1L);
          ps.setString(2, "username");
          ps.setString(3, "email@gmail.com");
          ps.setString(4, "password");
          return ps;
        });
    jdbcTemplate.update("INSERT INTO notifications(user_id, message) VALUES(?, ?)", 1L, "message");
  }

  @AfterEach
  void cleanUp() {
    jdbcTemplate.update("DELETE FROM notifications");
    jdbcTemplate.update("DELETE FROM users");
  }
}
