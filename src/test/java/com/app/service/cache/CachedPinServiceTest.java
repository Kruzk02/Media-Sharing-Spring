package com.app.service.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.app.module.hashtag.model.Hashtag;
import com.app.module.pin.application.dto.PinRequest;
import com.app.module.pin.application.service.CachedPinService;
import com.app.module.pin.application.service.PinService;
import com.app.module.pin.domain.Pin;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CachedPinServiceTest extends AbstractRedisTest<Pin> {

  private CachedPinService cachedPinService;
  private PinService mockPinService;

  private Pin pin;

  @BeforeEach
  @Override
  void setUp() {
    super.setUp();

    mockPinService = mock(PinService.class);
    cachedPinService = new CachedPinService(mockPinService, redisTemplate);

    pin =
        Pin.builder()
            .id(1L)
            .description("description")
            .userId(1L)
            .mediaId(1L)
            .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
            .build();
  }

  @Test
  @Order(1)
  void save() {
    when(mockPinService.save(new PinRequest("description", null, Set.of("tag")))).thenReturn(pin);

    Pin saved = cachedPinService.save(new PinRequest("description", null, Set.of("tag")));
    assertEquals(1L, saved.getId());
    assertEquals("description", saved.getDescription());
    assertEquals(1L, saved.getUserId());
    assertEquals(1L, saved.getMediaId());
    assertEquals(List.of(Hashtag.builder().id(1L).tag("tag").build()), saved.getHashtags());

    verify(mockPinService).save(new PinRequest("description", null, Set.of("tag")));
  }

  @Test
  @Order(2)
  void getAllPins() {
    List<Pin> pins = cachedPinService.getAllPins(SortType.NEWEST, 10, 0);
    assertTrue(pins.isEmpty());
  }

  @Test
  @Order(3)
  void getAllPinsByHashtag() {
    List<Pin> pins = cachedPinService.getAllPinsByHashtag("tag", 10, 0);
    assertTrue(pins.isEmpty());
  }

  @Test
  @Order(4)
  void findById() {
    Pin cached = cachedPinService.findById(1L, DetailsType.DETAIL);
    assertEquals(1L, cached.getId());
    assertEquals("description", cached.getDescription());
    assertEquals(1L, cached.getUserId());
    assertEquals(1L, cached.getMediaId());
    assertEquals(List.of(Hashtag.builder().id(1L).tag("tag").build()), cached.getHashtags());
  }

  @Test
  @Order(5)
  void findPinByUserId() {
    List<Pin> pins = cachedPinService.findPinByUserId(1L, 10, 0);
    assertTrue(pins.isEmpty());
  }

  @Test
  @Order(6)
  void update() {
    when(mockPinService.update(1L, new PinRequest("Description", null, Set.of("tag"))))
        .thenReturn(
            Pin.builder()
                .id(1L)
                .description("Description")
                .userId(1L)
                .mediaId(1L)
                .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
                .build());

    Pin cached = cachedPinService.update(1L, new PinRequest("Description", null, Set.of("tag")));
    assertEquals(1L, cached.getId());
    assertEquals("Description", cached.getDescription());
    assertEquals(1L, cached.getUserId());
    assertEquals(1L, cached.getMediaId());
    assertEquals(List.of(Hashtag.builder().id(1L).tag("tag").build()), cached.getHashtags());

    verify(mockPinService).update(1L, new PinRequest("Description", null, Set.of("tag")));
  }
}
