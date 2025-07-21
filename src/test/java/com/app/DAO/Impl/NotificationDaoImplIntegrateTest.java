package com.app.DAO.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.DAO.AbstractMySQLTest;
import com.app.DAO.NotificationDao;
import com.app.Model.Notification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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
    var result = notificationDao.save(Notification.builder().userId(1L).message("message").build());

    assertNotNull(result);
    assertEquals(1L, result.getUserId());
    assertEquals("message", result.getMessage());
  }

  @Test
  @Order(2)
  void findByUserId() {
    var result = notificationDao.findByUserId(1L, 10, 0, true);
    System.out.println(result);
    assertNotNull(result);
  }

  @AfterEach
  void cleanUp() {
    jdbcTemplate.update("DELETE FROM notifications");
    jdbcTemplate.update("DELETE FROM users");
  }
}
