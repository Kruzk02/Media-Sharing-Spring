package com.app.dao.base;

public interface Readable<T> {
  T findById(Long id);
}
