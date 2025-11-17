package com.app.user.dto.request;

import com.app.user.model.Gender;
import org.springframework.web.multipart.MultipartFile;

public record UpdateUserRequest(
    String username,
    String email,
    String password,
    String bio,
    Gender gender,
    MultipartFile profilePicture) {}
