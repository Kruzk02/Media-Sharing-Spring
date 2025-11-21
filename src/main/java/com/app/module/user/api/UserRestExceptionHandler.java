package com.app.module.user.api;

import com.app.module.user.application.exception.*;
import com.app.shared.exception.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = UserController.class)
public class UserRestExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
    return ResponseEntity.status(404)
        .body(
            new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
      UserAlreadyExistsException e) {
    return ResponseEntity.status(409)
        .body(new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(TokenExpireException.class)
  public ResponseEntity<ErrorResponse> handleTokenExpireException(TokenExpireException e) {
    return ResponseEntity.status(400)
        .body(
            new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
    return ResponseEntity.status(400)
        .body(
            new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
    return ResponseEntity.status(401)
        .body(
            new ErrorResponse(
                e.getMessage(), HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(UserEmptyException.class)
  public ResponseEntity<ErrorResponse> handleUserEmptyException(UserEmptyException e) {
    return ResponseEntity.status(400)
        .body(
            new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(UserValidationException.class)
  public ResponseEntity<ErrorResponse> handleUserValidationException(UserValidationException e) {
    return ResponseEntity.status(400)
        .body(
            new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()));
  }
}
