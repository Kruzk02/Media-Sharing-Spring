package com.app.module.board.application.exception;

public class PinNotInBoardException extends RuntimeException {
  public PinNotInBoardException(String message) {
    super(message);
  }
}
