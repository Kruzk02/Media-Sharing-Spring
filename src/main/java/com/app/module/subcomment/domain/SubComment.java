package com.app.module.subcomment.domain;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.modulith.NamedInterface;

@NamedInterface
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class SubComment implements Serializable {

  private long id;
  private String content;
  private Long commentId;
  private Long userId;
  private Long mediaId;
  private Instant createAt;
}
