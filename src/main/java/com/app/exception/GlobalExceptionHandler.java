package com.app.exception;

import com.app.exception.sub.*;
import io.jsonwebtoken.ExpiredJwtException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
    ErrorResponse response =
        new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CommentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCommentNotFoundException(CommentNotFoundException ex) {
    ErrorResponse response =
        new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BoardNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleBoardNotFoundException(BoardNotFoundException ex) {
    ErrorResponse response =
        new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(PinNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePinNotFoundException(PinNotFoundException ex) {
    ErrorResponse response =
        new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MediaNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleMediaNotFoundException(MediaNotFoundException ex) {
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

  @ExceptionHandler(SubCommentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleSubCommentNotFoundException(
      SubCommentNotFoundException e) {
    ErrorResponse response =
        new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(SaveDataFailedException.class)
  public ResponseEntity<ErrorResponse> handleSaveDataFieldException(SaveDataFailedException e) {
    ErrorResponse response =
        new ErrorResponse(
            e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
    ErrorResponse response =
        new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
      UserAlreadyExistsException e) {
    ErrorResponse response =
        new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MediaNotSupportException.class)
  public ResponseEntity<ErrorResponse> handleMediaNotSupportException(MediaNotSupportException e) {
    ErrorResponse response =
        new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TokenExpireException.class)
  public ResponseEntity<ErrorResponse> handleTokenExpireException(TokenExpireException e) {
    ErrorResponse response =
        new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(CommentIsEmptyException.class)
  public ResponseEntity<ErrorResponse> handleCommentIsEmptyException(CommentIsEmptyException e) {
    ErrorResponse response =
        new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
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

  @ExceptionHandler(PinNotInBoardException.class)
  public ResponseEntity<ErrorResponse> handlePinNotInBoardException(PinNotInBoardException e) {
    ErrorResponse response =
        new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NameValidationException.class)
  public ResponseEntity<ErrorResponse> handleNameValidationException(NameValidationException e) {
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
