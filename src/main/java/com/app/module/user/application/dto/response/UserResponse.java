package com.app.module.user.application.dto.response;

import com.app.module.user.domain.entity.User;
import com.app.module.user.domain.status.Gender;

public record UserResponse(
    Long id, String username, String email, long mediaId, String bio, Gender gender) {
  public static UserResponse fromEntity(User user) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getMediaId(),
        user.getBio(),
        user.getGender());
  }
}
