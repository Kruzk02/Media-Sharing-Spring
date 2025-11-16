package com.app.dto.response;

import com.app.model.Hashtag;
import com.app.model.Pin;
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
