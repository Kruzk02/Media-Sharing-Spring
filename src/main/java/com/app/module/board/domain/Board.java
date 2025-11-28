package com.app.module.board.domain;

import com.app.module.pin.domain.Pin;
import com.app.module.user.domain.entity.User;
import java.io.Serializable;
import java.util.List;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Board implements Serializable {

  private Long id;
  private User user;
  private String name;
  private List<Pin> pins;
}
