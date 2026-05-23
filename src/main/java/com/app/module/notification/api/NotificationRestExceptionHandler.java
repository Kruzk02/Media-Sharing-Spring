package com.app.module.notification.api;

import com.app.module.notification.application.exception.NotificationMessageIsEmptyException;
import com.app.module.notification.application.exception.NotificationNotFoundException;
import com.app.module.user.application.exception.UserNotFoundException;
import com.app.shared.exception.ErrorResponse;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = NotificationController.class)
public class NotificationRestExceptionHandler {
  @ExceptionHandler(NotificationNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotificationNotFoundException(
      NotificationNotFoundException ex) {
    return ResponseEntity.status(404)
        .body(
            new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
    return ResponseEntity.status(404)
        .body(
            new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(NotificationMessageIsEmptyException.class)
  public ResponseEntity<ErrorResponse> handleNotificationMessageIsEmptyException(
      NotificationMessageIsEmptyException ex) {
    return ResponseEntity.status(404)
        .body(
            new ErrorResponse(
                ex.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()));
  }
}
