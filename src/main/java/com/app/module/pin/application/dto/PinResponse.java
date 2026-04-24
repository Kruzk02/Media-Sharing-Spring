package com.app.module.pin.application.dto;

import com.app.module.hashtag.domain.Hashtag;
import com.app.module.pin.domain.Pin;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record PinResponse(
    Long id, Long userId, String description, Long mediaId, List<Hashtag> tag, Instant createdAt) {

  public static PinResponse fromEntity(Pin pin) {
    return new PinResponse(
        pin.getId(),
        pin.getUserId(),
        pin.getDescription(),
        pin.getMediaId(),
        pin.getHashtags() == null ? new ArrayList<>() : List.copyOf(pin.getHashtags()),
        pin.getCreatedAt());
  }
}
