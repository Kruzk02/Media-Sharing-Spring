package com.app.shared.exception.sub;

import org.springframework.modulith.NamedInterface;

@NamedInterface
public class PinNotFoundException extends RuntimeException {
  public PinNotFoundException(String message) {
    super(message);
  }
}
