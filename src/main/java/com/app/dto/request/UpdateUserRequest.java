package com.app.dto.request;

import com.app.model.Gender;
import org.springframework.web.multipart.MultipartFile;

public record UpdateUserRequest(
    String username,
    String email,
    String password,
    String bio,
    Gender gender,
    MultipartFile profilePicture) {}
