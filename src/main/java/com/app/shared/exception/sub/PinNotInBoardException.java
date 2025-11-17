package com.app.shared.exception.sub;

public class PinNotInBoardException extends RuntimeException {
  public PinNotInBoardException(String message) {
    super(message);
  }
}
