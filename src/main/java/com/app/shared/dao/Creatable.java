package com.app.shared.dao;

import org.springframework.modulith.NamedInterface;

@NamedInterface
public interface Creatable<T> {
  T save(T t);
}
