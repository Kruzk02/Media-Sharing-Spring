package com.app.module.pin.domain;

import com.app.module.hashtag.domain.Hashtag;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import lombok.*;
import org.springframework.modulith.NamedInterface;

@NamedInterface
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class Pin implements Serializable {

  private Long id;
  private Long userId;
  private String description;
  private Long mediaId;
  private Collection<Hashtag> hashtags;
  private Instant createdAt;
}
