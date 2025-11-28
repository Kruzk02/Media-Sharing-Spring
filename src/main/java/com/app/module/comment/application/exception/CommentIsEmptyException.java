package com.app.module.comment.application.exception;

public class CommentIsEmptyException extends RuntimeException {
  public CommentIsEmptyException(String message) {
    super(message);
  }
}
