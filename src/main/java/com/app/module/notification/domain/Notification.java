package com.app.module.notification.domain;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Notification {

  private Long id;
  private Long userId;
  private String message;
  private boolean isRead;
  private Instant createdAt;
}
