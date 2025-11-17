package com.app.shared.dao;

public interface Updatable<T> {
  T update(Long id, T t);
}
