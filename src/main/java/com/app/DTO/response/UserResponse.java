package com.app.DTO.response;

import com.app.Model.Gender;
import com.app.Model.User;

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
