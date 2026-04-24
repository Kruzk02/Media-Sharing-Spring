package com.app.module.comment.domain;

import com.app.module.hashtag.domain.Hashtag;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import lombok.*;
import org.springframework.modulith.NamedInterface;

@NamedInterface
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Comment implements Serializable {

  private Long id;
  private String content;
  private long pinId;
  private long userId;
  private long mediaId;
  private Collection<Hashtag> hashtags;
  private Instant created_at;
}
