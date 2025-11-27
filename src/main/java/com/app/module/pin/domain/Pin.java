package com.app.module.pin.domain;

import com.app.module.hashtag.model.Hashtag;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.*;

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
  private LocalDateTime createdAt;
}
