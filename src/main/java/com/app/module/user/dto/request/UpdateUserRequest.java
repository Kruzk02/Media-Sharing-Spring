package com.app.module.user.dto.request;

import com.app.module.user.domain.status.Gender;
import org.springframework.web.multipart.MultipartFile;

public record UpdateUserRequest(
    String username,
    String email,
    String password,
    String bio,
    Gender gender,
    MultipartFile profilePicture) {}
