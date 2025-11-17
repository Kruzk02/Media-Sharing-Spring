package com.app.shared.exception.sub;

public class UserNotMatchException extends RuntimeException {
  public UserNotMatchException(String message) {
    super(message);
  }
}
