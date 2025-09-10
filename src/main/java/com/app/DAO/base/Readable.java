package com.app.DAO.base;

import java.util.List;

public interface Readable<T> {
  T findById(Long id);

  List<T> findAll();
}
