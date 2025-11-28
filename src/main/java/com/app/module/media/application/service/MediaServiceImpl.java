package com.app.module.media.application.service;

import com.app.module.media.application.exception.MediaNotFoundException;
import com.app.module.media.domain.entity.Media;
import com.app.module.media.infrastructure.MediaDao;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Log4j2
@AllArgsConstructor
@Service
@Qualifier("mediaServiceImpl")
public class MediaServiceImpl implements MediaService {

  private final MediaDao mediaDao;

  @Override
  public Media findById(Long id) {
    Media media = mediaDao.findById(id);
    if (media == null) {
      throw new MediaNotFoundException("Media not found with a id: " + id);
    }
    return media;
  }
}
