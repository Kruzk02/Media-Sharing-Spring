package com.app.module.subcomment.api;

import com.app.module.subcomment.application.exception.SubCommentIsEmptyException;
import com.app.module.subcomment.domain.SubCommentNotFoundException;
import com.app.shared.exception.ErrorResponse;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = SubCommentController.class)
public class SubCommentRestExceptionHandler {

  @ExceptionHandler(SubCommentIsEmptyException.class)
  public ResponseEntity<ErrorResponse> handleSubCommentIsEmptyException(
      SubCommentIsEmptyException ex) {
    return ResponseEntity.status(404)
        .body(
            new ErrorResponse(
                ex.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()));
  }

  @ExceptionHandler(SubCommentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleSubCommentNotFoundException(
      SubCommentNotFoundException ex) {
    return ResponseEntity.status(404)
        .body(
            new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()));
  }
}
