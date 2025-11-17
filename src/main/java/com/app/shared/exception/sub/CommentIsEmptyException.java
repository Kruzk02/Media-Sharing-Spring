package com.app.shared.exception.sub;

public class CommentIsEmptyException extends RuntimeException {
  public CommentIsEmptyException(String message) {
    super(message);
  }
}
