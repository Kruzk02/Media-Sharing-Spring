package com.app.module.media.application.service;

import com.app.module.media.domain.entity.Media;

public interface MediaService {
  Media findById(Long id);
}
