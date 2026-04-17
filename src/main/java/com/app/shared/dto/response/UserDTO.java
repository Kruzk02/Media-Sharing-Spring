package com.app.shared.dto.response;

import org.springframework.modulith.NamedInterface;

@NamedInterface
public record UserDTO(Long id, String username) {}
