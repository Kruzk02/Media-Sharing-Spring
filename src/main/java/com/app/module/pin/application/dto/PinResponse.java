package com.app.module.pin.application.dto;

import com.app.module.hashtag.model.Hashtag;
import com.app.module.pin.domain.Pin;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record PinResponse(
    Long id,
    Long userId,
    String description,
    Long mediaId,
    List<Hashtag> tag,
    LocalDateTime createdAt) {

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
