package com.app.DAO.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.DAO.AbstractMySQLTest;
import com.app.DAO.privilege.PrivilegeDao;
import com.app.DAO.privilege.PrivilegeDaoImpl;
import com.app.Model.Privilege;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
class PrivilegeDaoImplIntegrateTest extends AbstractMySQLTest {

  private PrivilegeDao privilegeDao;

  @BeforeEach
  void setUp() {
    privilegeDao = new PrivilegeDaoImpl(jdbcTemplate);
  }

  @Test
  @Order(1)
  void save() {
    var saved = privilegeDao.save(Privilege.builder().id(1L).name("name").build());

    assertNotNull(saved);
    assertEquals("name", saved.getName());
  }

  @Test
  @Order(2)
  void findByName() {
    var found = privilegeDao.findByName("name");

    assertNotNull(found);
    assertEquals("name", found.getName());
  }
}
