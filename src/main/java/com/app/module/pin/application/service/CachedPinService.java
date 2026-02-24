package com.app.module.pin.application.service;

import com.app.module.pin.application.dto.PinKeysetResponse;
import com.app.module.pin.application.dto.PinRequest;
import com.app.module.pin.domain.Pin;
import com.app.shared.exception.sub.PinNotFoundException;
import com.app.shared.helper.CachedServiceHelper;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.io.IOException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

/**
 * Caching decorator for {@link PinService}.
 *
 * <p>Delegates business logic to the primary {@link PinService} implementation while caching read
 * operations in Redis.
 *
 * <p>Cache entries are invalidated on write operations to maintain consistency.
 */
@Slf4j
@Component
@Primary
public class CachedPinService extends CachedServiceHelper<Pin> implements PinService {

  private final PinService delegate;

  public CachedPinService(
      @Qualifier("pinServiceImpl") PinService pinServiceImpl,
      RedisTemplate<String, Pin> pinRedisTemplate) {
    super(pinRedisTemplate);
    this.delegate = pinServiceImpl;
  }

  @Override
  public PinKeysetResponse getAllPins(SortType sortType, int limit, String cursor) {
    // Only cached the first page.
    // The second and afterward not cached.
    if (cursor != null) {
      return delegate.getAllPins(sortType, limit, cursor);
    }

    String key = "Pins:zset:" + sortType;
    ZSetOperations<String, Pin> zSet = redisTemplate.opsForZSet();

    Long size = zSet.zCard(key);

    if (size != null && size >= limit) {
      Set<Pin> cached = zSet.reverseRange(key, 0, limit - 1);
      if (cached != null && !cached.isEmpty()) {
        return new PinKeysetResponse(new ArrayList<>(cached), null);
      }
    }

    PinKeysetResponse response = delegate.getAllPins(sortType, limit, null);

    for (Pin pin : response.pins()) {
      zSet.add(key, pin, pin.getCreatedAt().toEpochSecond(ZoneOffset.UTC));
    }

    redisTemplate.expire(key, Duration.ofMinutes(5));

    return response;
  }

  @Override
  public List<Pin> getAllPinsByHashtag(String tag, int limit, int offset) {
    var redisKey = "pins_hashtag:" + tag + ":limit:" + limit + ":offset:" + offset;
    return super.getListOrLoad(
        redisKey,
        () -> delegate.getAllPinsByHashtag(tag, limit, offset),
        limit,
        offset,
        Duration.ofHours(2));
  }

  @Override
  public Pin save(PinRequest pinRequest) {
    var pin = delegate.save(pinRequest);
    var cached = super.getOrLoad("pin:" + pin.getId() + ":basic", () -> pin, Duration.ofHours(2));
    return cached.orElse(pin);
  }

  @Override
  public Pin update(Long id, PinRequest pinRequest) {
    var pin = delegate.update(id, pinRequest);
    super.delete("pin:" + id + ":basic");
    super.delete("pin:" + id + ":details");
    var cached = super.getOrLoad("pin:" + pin.getId() + ":basic", () -> pin, Duration.ofHours(2));
    return cached.orElse(pin);
  }

  @Override
  public Pin findById(Long id, DetailsType detailsType) {
    var cacheKey =
        detailsType.getType().equals("DETAIL") ? "pin:" + id + ":details" : "pin:" + id + ":basic";
    var cached =
        super.getOrLoad(cacheKey, () -> delegate.findById(id, detailsType), Duration.ofHours(2));
    return cached.orElseThrow(() -> new PinNotFoundException("Pin not found with a id: " + id));
  }

  @Override
  public List<Pin> findPinByUserId(Long userId, int limit, int offset) {
    var redisKey = "user:" + userId + ":pins";
    return super.getListOrLoad(
        redisKey,
        () -> delegate.findPinByUserId(userId, limit, offset),
        limit,
        offset,
        Duration.ofHours(2));
  }

  @Override
  public void delete(Long id) throws IOException {
    var pin = delegate.findById(id, DetailsType.BASIC);
    super.delete("pin:*");
    super.delete("user:" + pin.getUserId() + ":pins");
    delegate.delete(id);
  }
}
