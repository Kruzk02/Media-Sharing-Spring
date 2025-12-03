package com.app.module.media.application.service;

import com.app.module.media.application.exception.MediaNotFoundException;
import com.app.module.media.domain.entity.Media;
import com.app.shared.helper.CachedServiceHelper;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Primary
public class CachedMediaService extends CachedServiceHelper<Media> implements MediaService {

  private final MediaService mediaService;

  public CachedMediaService(
      RedisTemplate<String, Media> mediaRedisTemplate,
      @Qualifier("mediaServiceImpl") MediaService mediaService) {
    super(mediaRedisTemplate);
    this.mediaService = mediaService;
  }

  @Override
  public Media findById(Long id) {
    var cached =
        super.getOrLoad("media:" + id, () -> mediaService.findById(id), Duration.ofMinutes(30));
    return cached.orElseThrow(() -> new MediaNotFoundException("Media not found with a id: " + id));
  }
}
