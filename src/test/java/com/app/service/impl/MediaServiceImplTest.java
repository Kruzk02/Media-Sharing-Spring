package com.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.module.media.dao.MediaDao;
import com.app.module.media.model.Media;
import com.app.module.media.model.MediaType;
import com.app.module.media.service.MediaServiceImpl;
import com.app.shared.exception.sub.MediaNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MediaServiceImplTest {

  @Mock private MediaDao mediaDao;

  @InjectMocks private MediaServiceImpl mediaService;

  private Media media;

  @BeforeEach
  void setUp() {
    media = Media.builder().id(1L).url("url").mediaType(MediaType.IMAGE).build();
  }

  @Test
  void findById_shouldReturnMedia() {
    Mockito.when(mediaDao.findById(1L)).thenReturn(media);
    var result = mediaService.findById(1L);

    assertNotNull(result);
    assertEquals(media, result);
  }

  @Test
  void findById_shouldThrowException() {
    Mockito.when(mediaDao.findById(1L)).thenReturn(null);
    var ex = assertThrows(MediaNotFoundException.class, () -> mediaService.findById(1L));
    assertEquals("Media not found with a id: 1", ex.getMessage());
  }
}
