package com.app.shared.type;

import lombok.Getter;

@Getter
public enum SortType {
  NEWEST("DESC"),
  OLDEST("ASC");

  private final String order;

  SortType(String order) {
    this.order = order;
  }
}
