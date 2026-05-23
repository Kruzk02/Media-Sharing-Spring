package com.app.module.user.domain.entity;

import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationToken implements Serializable {

  private Long id;
  private String token;
  private Long userId;
  private Instant createAt;
  private Instant expireDate;
}
