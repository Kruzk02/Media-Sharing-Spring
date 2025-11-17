package com.app.shared.dao;

public interface Readable<T> {
  T findById(Long id);
}
