package com.app.dao.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.dao.AbstractMySQLTest;
import com.app.user.dao.verificationtoken.VerificationTokenDao;
import com.app.user.dao.verificationtoken.VerificationTokenDaoImpl;
import com.app.user.model.VerificationToken;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VerificationTokenDaoImplIntegrateTest extends AbstractMySQLTest {

  private VerificationTokenDao verificationTokenDao;

  @BeforeEach
  void setUp() {
    verificationTokenDao = new VerificationTokenDaoImpl(jdbcTemplate);
  }

  @Test
  @Order(1)
  void save() {
    var result =
        verificationTokenDao.save(
            VerificationToken.builder()
                .token("token")
                .userId(1L)
                .expireDate(LocalDateTime.now().plusMinutes(10))
                .build());

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("token", result.getToken());
    assertEquals(1L, result.getUserId());
  }

  @Test
  @Order(2)
  void findByToken() {
    var result = verificationTokenDao.findByToken("token");

    assertNotNull(result);
    assertEquals("token", result.getToken());
    assertEquals(1L, result.getUserId());
  }

  @Test
  @Order(3)
  void deleteByToken() {
    var result = verificationTokenDao.deleteByToken("token");

    assertEquals(1, result);
  }
}
