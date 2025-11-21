package com.app.module.user.application.exception;

public class UserValidationException extends RuntimeException {
  public UserValidationException(String message) {
    super(message);
  }
}
