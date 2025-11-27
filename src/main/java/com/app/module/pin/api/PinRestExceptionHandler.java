package com.app.module.pin.api;

import com.app.module.pin.application.exception.PinIsEmptyException;
import com.app.shared.exception.ErrorResponse;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PinController.class)
public class PinRestExceptionHandler {
  @ExceptionHandler(PinIsEmptyException.class)
  public ResponseEntity<ErrorResponse> handlePinIsEmptyException(PinIsEmptyException ex) {
    return ResponseEntity.status(400)
        .body(
            new ErrorResponse(
                ex.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()));
  }
}
