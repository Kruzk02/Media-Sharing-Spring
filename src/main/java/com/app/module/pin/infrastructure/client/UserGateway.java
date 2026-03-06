package com.app.module.pin.infrastructure.client;

import com.app.module.pin.infrastructure.dto.UserDto;

public interface UserGateway {
  UserDto getUserByUsername(String username);
}
