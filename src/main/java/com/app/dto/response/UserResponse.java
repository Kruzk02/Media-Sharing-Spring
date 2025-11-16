package com.app.dto.response;

import com.app.model.Gender;
import com.app.model.User;

public record UserResponse(
    Long id, String username, String email, long mediaId, String bio, Gender gender) {
  public static UserResponse fromEntity(User user) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getMedia().getId(),
        user.getBio(),
        user.getGender());
  }
}
