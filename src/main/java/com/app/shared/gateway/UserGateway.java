package com.app.shared.gateway;


import com.app.shared.dto.response.UserDTO;

public interface UserGateway {
  UserDTO getUserByUsername(String username);
}
