package com.app.shared.exception.sub;

public class PinIsEmptyException extends RuntimeException {
  public PinIsEmptyException(String message) {
    super(message);
  }
}
