package com.app.exception.sub;

public class FileSaveException extends RuntimeException {

  public FileSaveException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
