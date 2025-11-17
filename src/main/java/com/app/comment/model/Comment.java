package com.app.comment.model;

import com.app.model.Hashtag;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.*;

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
  private LocalDateTime created_at;
}
