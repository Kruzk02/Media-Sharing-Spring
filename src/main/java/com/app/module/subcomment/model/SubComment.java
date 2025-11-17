package com.app.module.subcomment.model;

import com.app.module.comment.model.Comment;
import com.app.module.media.model.Media;
import com.app.module.user.model.User;
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
