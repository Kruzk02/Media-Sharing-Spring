package com.app.module.hashtag.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Hashtag implements Serializable {
  private Long id;
  private String tag;
  private LocalDateTime createdAt;
}
