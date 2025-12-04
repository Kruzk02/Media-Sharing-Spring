package com.app.dao;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractMySQLTest {

  @Container static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8");

  protected JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUpBase() {
    DataSource dataSource =
        DataSourceBuilder.create()
            .url(mysql.getJdbcUrl() + "?useSSL=false&allowPublicKeyRetrieval=true")
            .username(mysql.getUsername())
            .password(mysql.getPassword())
            .driverClassName(mysql.getDriverClassName())
            .build();

    jdbcTemplate = new JdbcTemplate(dataSource);

    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS media("
            + "id INT AUTO_INCREMENT PRIMARY KEY,"
            + "url VARCHAR(500) NOT NULL,"
            + "media_type ENUM('VIDEO', 'IMAGE') NOT NULL,"
            + "status ENUM('PENDING', 'FAILED', 'READY'),"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS users ("
            + "id INT AUTO_INCREMENT PRIMARY KEY,"
            + "username VARCHAR(255) NOT NULL,"
            + "email VARCHAR(255) NOT NULL UNIQUE,"
            + "password VARCHAR(255) NOT NULL,"
            + "bio TEXT,"
            + "gender ENUM('male', 'female', 'other') NOT NULL,"
            + "enable BOOLEAN,"
            + "media_id INT,"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS roles ("
            + "id int auto_increment primary key,"
            + "name VARCHAR(255)"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS privileges("
            + "id INT auto_increment PRIMARY KEY,"
            + "name varchar(255) UNIQUE"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS roles_privileges ("
            + "role_id int,"
            + "privilege_id int,"
            + "FOREIGN KEY (privilege_id) REFERENCES privileges(id),"
            + "FOREIGN KEY (role_id) REFERENCES roles(id)"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS users_roles("
            + "role_id int,"
            + "user_id int,"
            + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,"
            + "FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS verification_token ("
            + "id INT AUTO_INCREMENT PRIMARY KEY,"
            + "token VARCHAR(255) UNIQUE NOT NULL,"
            + "user_id INT NOT NULL,"
            + "expiration_date DATETIME NOT NULL,"
            + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE\n"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS notifications("
            + "id INT auto_increment PRIMARY KEY,"
            + "user_id INT NOT NULL,"
            + "message VARCHAR(512) NOT NULL,"
            + "is_read BOOLEAN DEFAULT FALSE,"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,"
            + "INDEX(user_id)"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS pins ("
            + "id int auto_increment primary key,"
            + "user_id int NOT NULL,"
            + "description text,"
            + "media_id int NULL DEFAULT NULL,"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,"
            + "FOREIGN kEY (media_id) REFERENCES media(id) ON DELETE CASCADE"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS comments ("
            + "id INT AUTO_INCREMENT PRIMARY KEY,"
            + "content TEXT,"
            + "user_id INT,"
            + "pin_id INT,"
            + "media_id INT,"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,"
            + "FOREIGN KEY (pin_id) REFERENCES pins(id) ON DELETE CASCADE,"
            + "FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS followers("
            + "follower_id INT NOT NULL,"
            + "following_id INT NOT NULL,"
            + "following_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY (follower_id, following_id),"
            + "FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,"
            + "FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS hashtags("
            + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
            + "tag VARCHAR(69) NOT NULL UNIQUE,"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS hashtags_pins("
            + "hashtag_id INT,"
            + "pin_id INT,"
            + "FOREIGN KEY (hashtag_id) REFERENCES hashtags(id) ON DELETE CASCADE,"
            + "FOREIGN KEY (pin_id) REFERENCES pins(id) ON DELETE CASCADE"
            + ")");

    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS comments ("
            + "d INT AUTO_INCREMENT PRIMARY KEY,"
            + "content TEXT,"
            + "user_id INT,"
            + "pin_id INT,"
            + "media_id INT,"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,"
            + "FOREIGN KEY (pin_id) REFERENCES pins(id) ON DELETE CASCADE,"
            + "FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE"
            + ")");

    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS hashtags_comments("
            + "hashtag_id INT,"
            + "comment_id INT,"
            + "FOREIGN KEY (hashtag_id) REFERENCES hashtags(id) ON DELETE CASCADE,"
            + "FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE"
            + ")");

    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS boards ("
            + "id int auto_increment primary key,"
            + "user_id int,"
            + "board_name varchar(255) not null,"
            + "create_at timestamp default current_timestamp,"
            + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
            + ")");

    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS board_pin ("
            + "board_id INT,"
            + "pin_id INT,"
            + "FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE,"
            + "FOREIGN KEY (pin_id) REFERENCES pins(id) ON DELETE CASCADE"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS sub_comments ("
            + "id INT AUTO_INCREMENT PRIMARY KEY,"
            + "content TEXT,"
            + "user_id INT,"
            + "comment_id INT,"
            + "media_id INT,"
            + "create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,"
            + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,"
            + "FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE"
            + ")");

    jdbcTemplate.update(
        "INSERT IGNORE INTO media (id, url, media_type, status) VALUES (?, ?, ?, ?)",
        1,
        "url",
        "IMAGE",
        "PENDING");
    jdbcTemplate.update("INSERT IGNORE INTO roles (id, name) VALUES (?, ?)", 2, "ROLE_USER");
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

    jdbcTemplate.update(
        "INSERT IGNORE INTO users (id, username, email, password, gender, bio, enable, media_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
        2,
        "username2",
        "email2@gmail.com",
        "HashedPassword",
        "male",
        "bio",
        false,
        1);
    jdbcTemplate.update("INSERT INTO users_roles (user_id, role_id) VALUES (?, ?)", 2, 2);
  }
}
