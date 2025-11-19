package com.app.module.user.application.exception;

public class TokenExpireException extends RuntimeException {
  public TokenExpireException(String message) {
    super(message);
  }
}
