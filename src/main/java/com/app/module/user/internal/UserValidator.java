package com.app.module.user.internal;

import com.app.module.user.application.exception.UserEmptyException;
import com.app.module.user.application.exception.UserValidationException;
import java.util.regex.Pattern;

public class UserValidator {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

  private static final Pattern PASSWORD_PATTERN =
      Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

  public static void validateUsername(String username) {
    if (username == null || username.isBlank()) {
      throw new UserEmptyException("Username cannot be empty");
    }
  }

  public static void validateEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new UserEmptyException("Email cannot be empty");
    }

    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new UserValidationException("Invalid email format");
    }
  }

  public static void validatePassword(String password) {
    if (password == null || password.isBlank()) {
      throw new UserEmptyException("Password cannot be empty");
    }

    if (!PASSWORD_PATTERN.matcher(password).matches()) {
      throw new UserValidationException(
          "Password must be 8+ chars, include uppercase, lowercase, digit, special char");
    }
  }
}
