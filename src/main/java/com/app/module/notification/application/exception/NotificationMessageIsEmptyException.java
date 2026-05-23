package com.app.module.notification.application.exception;

public class NotificationMessageIsEmptyException extends RuntimeException {
  public NotificationMessageIsEmptyException(String message) {
    super(message);
  }
}
