package com.app.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterUserRequest(
    @NotNull String username, @Email String email, @NotNull String password) {}
