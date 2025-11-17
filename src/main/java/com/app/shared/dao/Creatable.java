package com.app.shared.dao;

public interface Creatable<T> {
  T save(T t);
}
