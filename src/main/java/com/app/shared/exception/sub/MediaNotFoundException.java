package com.app.shared.exception.sub;

public class MediaNotFoundException extends RuntimeException {
  public MediaNotFoundException(String message) {
    super(message);
  }
}
