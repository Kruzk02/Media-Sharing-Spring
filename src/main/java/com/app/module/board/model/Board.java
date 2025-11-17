package com.app.module.board.model;

import com.app.module.pin.model.Pin;
import com.app.module.user.model.User;
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
