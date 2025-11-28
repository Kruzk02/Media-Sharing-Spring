package com.app.shared.exception;

import com.app.shared.exception.sub.*;
import com.app.shared.exception.sub.UserNotMatchException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(PinNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePinNotFoundException(PinNotFoundException ex) {
    ErrorResponse response =
        new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UserNotMatchException.class)
  public ResponseEntity<ErrorResponse> handleUserNotMatchException(UserNotMatchException ex) {
    ErrorResponse response =
        new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(SaveDataFailedException.class)
  public ResponseEntity<ErrorResponse> handleSaveDataFieldException(SaveDataFailedException e) {
    ErrorResponse response =
        new ErrorResponse(
            e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(MediaNotSupportException.class)
  public ResponseEntity<ErrorResponse> handleMediaNotSupportException(MediaNotSupportException e) {
    ErrorResponse response =
        new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleFileNotFoundException(FileNotFoundException e) {
    ErrorResponse response =
        new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(FileSaveException.class)
  public ResponseEntity<ErrorResponse> handleFileSaveException(FileSaveException e) {
    ErrorResponse response =
        new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(FileDeleteException.class)
  public ResponseEntity<ErrorResponse> handleFileDeleteException(FileDeleteException e) {
    ErrorResponse response =
        new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
}
