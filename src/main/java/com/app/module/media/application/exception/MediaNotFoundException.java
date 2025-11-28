package com.app.module.media.application.exception;

public class MediaNotFoundException extends RuntimeException {
  public MediaNotFoundException(String message) {
    super(message);
  }
}
