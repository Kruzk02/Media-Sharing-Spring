package com.app.module.subcomment.domain;

import com.app.module.comment.domain.Comment;
import com.app.module.media.domain.entity.Media;
import com.app.module.user.domain.entity.User;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class SubComment implements Serializable {

  private long id;
  private String content;
  private Comment comment;
  private User user;
  private Media media;
  private LocalDateTime createAt;
}
