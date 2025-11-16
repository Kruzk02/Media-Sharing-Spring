package com.app.dao.base;

public interface Creatable<T> {
  T save(T t);
}
