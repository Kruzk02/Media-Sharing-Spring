package com.app.shared.exception.sub;

public class TokenExpireException extends RuntimeException {
  public TokenExpireException(String message) {
    super(message);
  }
}
