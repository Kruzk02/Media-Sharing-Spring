package com.app.DAO.base;

public interface Creatable<T> {
  T save(T t);
}
