package com.app.shared.exception;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.modulith.NamedInterface;

@NamedInterface
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
  private String message;
  private int httpStatus;
  private LocalDateTime timestamp;
}
