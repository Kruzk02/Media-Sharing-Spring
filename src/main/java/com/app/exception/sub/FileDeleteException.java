package com.app.exception.sub;

public class FileDeleteException extends RuntimeException {

  public FileDeleteException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
