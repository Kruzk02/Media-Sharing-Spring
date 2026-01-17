package com.app.module.pin.application.service;

import com.app.module.pin.application.dto.PinRequest;
import com.app.module.pin.domain.Pin;
import com.app.shared.exception.sub.PinNotFoundException;
import com.app.shared.helper.CachedServiceHelper;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
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
  public List<Pin> getAllPins(SortType sortType, int limit, int offset) {
    var redisKey = "pins:" + sortType + ":limit:" + limit + ":offset:" + offset;
    return super.getListOrLoad(
        redisKey,
        () -> delegate.getAllPins(sortType, limit, offset),
        limit,
        offset,
        Duration.ofHours(2));
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
