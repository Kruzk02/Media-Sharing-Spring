package com.app.module.hashtag.domain;

import java.io.Serializable;
import java.time.Instant;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Hashtag implements Serializable {
  private Long id;
  private String tag;
  private Instant createdAt;
}
