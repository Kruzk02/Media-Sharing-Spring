package com.app.DAO.base;

public interface Updatable<T> {
  T update(Long id, T t);
}
