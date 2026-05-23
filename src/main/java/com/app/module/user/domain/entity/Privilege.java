package com.app.module.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.modulith.NamedInterface;

@NamedInterface
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Privilege {
  private Long id;
  private String name;

  public Privilege(String name) {
    this.name = name;
  }
}
