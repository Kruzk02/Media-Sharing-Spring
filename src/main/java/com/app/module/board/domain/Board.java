package com.app.module.board.domain;

import java.io.Serializable;
import java.util.List;
import lombok.*;
import org.springframework.modulith.NamedInterface;

@NamedInterface
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Board implements Serializable {

  private Long id;
  private Long userId;
  private String name;
  private List<Long> pins;
}
