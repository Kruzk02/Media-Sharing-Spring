package com.app.module.media.api;

import com.app.module.media.application.exception.MediaNotFoundException;
import com.app.shared.exception.ErrorResponse;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = MediaController.class)
public class MediaRestExceptionHandler {
  @ExceptionHandler(MediaNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleMediaNotFoundException(MediaNotFoundException ex) {
    return ResponseEntity.status(404)
        .body(
            new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()));
  }
}
