package com.app.module.board.api;

import com.app.module.board.application.exception.NameValidationException;
import com.app.module.board.application.exception.PinNotInBoardException;
import com.app.module.board.domain.BoardNotFoundException;
import com.app.shared.exception.ErrorResponse;
import com.app.shared.exception.sub.PinAlreadyExistingException;
import com.app.shared.exception.sub.PinNotFoundException;
import com.app.shared.exception.sub.UserNotMatchException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = BoardController.class)
public class BoardRestExceptionHandler {
  @ExceptionHandler(NameValidationException.class)
  public ResponseEntity<ErrorResponse> handleNameValidationException(NameValidationException ex) {
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

  @ExceptionHandler(BoardNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleBoardNotFoundException(BoardNotFoundException ex) {
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

  @ExceptionHandler(PinAlreadyExistingException.class)
  public ResponseEntity<ErrorResponse> handlePinAlreadyExistingException(
      PinAlreadyExistingException ex) {
    return ResponseEntity.status(400)
        .body(
            new ErrorResponse(
                ex.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(PinNotInBoardException.class)
  public ResponseEntity<ErrorResponse> handlePinNotInBoardException(PinNotInBoardException ex) {
    return ResponseEntity.status(400)
        .body(
            new ErrorResponse(
                ex.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()));
  }
}
