package com.app.DAO.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.DAO.AbstractMySQLTest;
import com.app.DAO.user.UserDao;
import com.app.DAO.user.UserDaoImpl;
import com.app.Model.*;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoIntegrationTest extends AbstractMySQLTest {

  private UserDao userDao;

  @BeforeEach
  void setUp() throws Exception {
    userDao = new UserDaoImpl(jdbcTemplate);
  }

  @Test
  @Order(1)
  void register() {
    User savedUser =
        userDao.register(
            User.builder()
                .id(1L)
                .username("username3")
                .email("email3@gmail.com")
                .password("HashedPassword")
                .gender(Gender.MALE)
                .media(Media.builder().id(1L).mediaType(MediaType.IMAGE).url("url").build())
                .roles(
                    List.of(
                        Role.builder()
                            .id(2L)
                            .name("ROLE_USER")
                            .privileges(List.of(Privilege.builder().id(2L).name("READ").build()))
                            .build()))
                .bio("bio")
                .enable(false)
                .build());

    assertNotNull(savedUser);
    assertNotNull(savedUser.getId());

    System.out.println(savedUser.getId());
    Integer count =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE id = ? AND username = ?",
            Integer.class,
            savedUser.getId(),
            "username3");

    assertEquals(1, count);

    Integer roleCount =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users_roles WHERE user_id = ? AND role_id = 2",
            Integer.class,
            savedUser.getId());

    assertEquals(1, roleCount);
  }

  @Test
  @Order(2)
  void login() {
    insertTestUser();
    var savedUser = userDao.login("username2");

    assertNotNull(savedUser);
    assertEquals(2L, savedUser.getId());
    assertEquals("username2", savedUser.getUsername());
    assertEquals("email2@gmail.com", savedUser.getEmail());
    assertFalse(savedUser.getEnable());
  }

  @Test
  @Order(3)
  void findUserById() {
    insertTestUser();
    var foundUser = userDao.findUserById(1L);

    assertNotNull(foundUser);
    assertEquals(1L, foundUser.getId());
    assertEquals("username", foundUser.getUsername());
    assertEquals("email@gmail.com", foundUser.getEmail());
    assertFalse(foundUser.getEnable());
  }

  @Test
  @Order(4)
  void findUserByUsername() {
    insertTestUser();
    var foundUser = userDao.findUserByUsername("username");

    assertNotNull(foundUser);
    assertEquals(1L, foundUser.getId());
    assertEquals("username", foundUser.getUsername());
    assertEquals("email@gmail.com", foundUser.getEmail());
    assertFalse(foundUser.getEnable());
  }

  @Test
  @Order(5)
  void findUserByEmail() {
    insertTestUser();
    var foundUser = userDao.findUserByEmail("email@gmail.com");

    assertNotNull(foundUser);
    assertEquals(1L, foundUser.getId());
    assertEquals("username", foundUser.getUsername());
    assertEquals("email@gmail.com", foundUser.getEmail());
    assertFalse(foundUser.getEnable());
  }

  @Test
  @Order(6)
  void findFullUserByUsername() {
    insertTestUser();
    User result = userDao.findFullUserByUsername("username");

    assertNotNull(result);
    assertEquals("username", result.getUsername());
    assertEquals("email@gmail.com", result.getEmail());
    assertEquals(Gender.MALE, result.getGender());
    assertFalse(result.getEnable());

    assertNotNull(result.getMedia());
  }

  @Test
  @Order(7)
  void findPasswordNRoleByUsername() {
    insertTestUser();

    User result = userDao.findPasswordNRoleByUsername("username");

    assertNotNull(result);
    assertEquals("username", result.getUsername());
    assertEquals("HashedPassword", result.getPassword());

    assertNotNull(result.getRoles());
  }

  @Test
  @Order(8)
  void update() {
    insertTestUser();

    var result =
        userDao.update(
            User.builder()
                .id(1L)
                .username("username123")
                .email("email123@gmail.com")
                .password("HashedPassword")
                .gender(Gender.MALE)
                .media(Media.builder().id(1L).mediaType(MediaType.IMAGE).url("url").build())
                .roles(
                    List.of(
                        Role.builder()
                            .id(2L)
                            .name("ROLE_USER")
                            .privileges(List.of(Privilege.builder().id(2L).name("READ").build()))
                            .build()))
                .bio("bio")
                .enable(false)
                .build());

    assertNotNull(result);
    assertEquals("username123", result.getUsername());
    assertEquals("email123@gmail.com", result.getEmail());
    assertEquals("HashedPassword", result.getPassword());
    assertEquals(Gender.MALE, result.getGender());
    assertFalse(result.getEnable());

    assertNotNull(result.getMedia());
  }

  @Test
  @Order(9)
  void checkAccountVerifyById() {
    insertTestUser();

    var result = userDao.checkAccountVerifyById(1L);

    assertFalse(result);
  }

  private void insertTestUser() {
    jdbcTemplate.update(
        "INSERT IGNORE INTO users (id, username, email, password, gender, bio, enable, media_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
        1,
        "username",
        "email@gmail.com",
        "HashedPassword",
        "male",
        "bio",
        false,
        1);
    jdbcTemplate.update("INSERT INTO users_roles (user_id, role_id) VALUES (?, ?)", 1, 2);
  }

  @AfterEach
  void cleanUp() {
    jdbcTemplate.update("DELETE FROM users_roles");
    jdbcTemplate.update("DELETE FROM users");
  }
}
