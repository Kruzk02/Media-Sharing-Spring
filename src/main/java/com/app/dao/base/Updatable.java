package com.app.dao.base;

public interface Updatable<T> {
  T update(Long id, T t);
}
