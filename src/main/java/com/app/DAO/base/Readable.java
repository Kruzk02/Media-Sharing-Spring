package com.app.DAO.base;

public interface Readable<T> {
  T findById(Long id);
}
