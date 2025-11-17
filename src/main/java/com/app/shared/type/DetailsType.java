package com.app.shared.type;

import lombok.Getter;

@Getter
public enum DetailsType {
  DETAIL("DETAIL"),
  BASIC("BASIC");

  private final String type;

  DetailsType(String type) {
    this.type = type;
  }
}
