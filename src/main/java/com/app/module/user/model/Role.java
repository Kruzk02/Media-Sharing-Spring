package com.app.module.user.model;

import java.util.Collection;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Role {

  private Long id;
  private String name;
  private Collection<Privilege> privileges;

  public Role(String name) {
    this.name = name;
  }
}
