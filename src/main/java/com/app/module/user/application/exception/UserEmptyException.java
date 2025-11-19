package com.app.module.user.application.exception;

public class UserEmptyException extends RuntimeException {
  public UserEmptyException(String message) {
    super(message);
  }
}
