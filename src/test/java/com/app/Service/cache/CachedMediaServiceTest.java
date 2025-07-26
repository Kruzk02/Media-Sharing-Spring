package com.app.Service.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.app.Model.Media;
import com.app.Model.MediaType;
import com.app.Service.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CachedMediaServiceTest extends AbstractRedisTest<Media> {

  private CachedMediaService cachedMediaService;
  private MediaService mediaService;

  private Media media;

  @BeforeEach
  @Override
  void setUp() {
    super.setUp();

    mediaService = mock(MediaService.class);
    cachedMediaService = new CachedMediaService(redisTemplate, mediaService);

    media = Media.builder().id(1L).url("url").mediaType(MediaType.IMAGE).build();
  }

  @Test
  void findById() {
    when(mediaService.findById(1L)).thenReturn(media);
    Media cached = cachedMediaService.findById(1L);

    assertEquals(1L, cached.getId());
    assertEquals("url", cached.getUrl());
    assertEquals(MediaType.IMAGE, cached.getMediaType());
  }
}
