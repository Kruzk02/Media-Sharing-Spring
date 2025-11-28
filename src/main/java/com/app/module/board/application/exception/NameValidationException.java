package com.app.module.board.application.exception;

public class NameValidationException extends RuntimeException {
  public NameValidationException(String message) {
    super(message);
  }
}
