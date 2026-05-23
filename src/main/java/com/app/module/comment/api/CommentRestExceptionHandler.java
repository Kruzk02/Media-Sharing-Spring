package com.app.module.comment.api;

import com.app.module.comment.application.exception.CommentIsEmptyException;
import com.app.module.comment.domain.CommentNotFoundException;
import com.app.shared.exception.ErrorResponse;
import com.app.shared.exception.sub.PinNotFoundException;
import com.app.shared.exception.sub.UserNotMatchException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = CommentController.class)
public class CommentRestExceptionHandler {
  @ExceptionHandler(CommentIsEmptyException.class)
  public ResponseEntity<ErrorResponse> handleCommentIsEmptyException(CommentIsEmptyException ex) {
    return ResponseEntity.status(400)
        .body(
            new ErrorResponse(
                ex.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(PinNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePinNotFoundException(PinNotFoundException ex) {
    return ResponseEntity.status(404)
        .body(
            new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(CommentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCommentNotFoundException(CommentNotFoundException ex) {
    return ResponseEntity.status(404)
        .body(
            new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(UserNotMatchException.class)
  public ResponseEntity<ErrorResponse> handleUserNotMatchException(UserNotMatchException ex) {
    return ResponseEntity.status(403)
        .body(
            new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value(), LocalDateTime.now()));
  }
}
