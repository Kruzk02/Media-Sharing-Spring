package com.app.module.pin.application.exception;

public class PinIsEmptyException extends RuntimeException {
  public PinIsEmptyException(String message) {
    super(message);
  }
}
