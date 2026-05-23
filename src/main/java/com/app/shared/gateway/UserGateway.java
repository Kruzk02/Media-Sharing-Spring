package com.app.shared.gateway;

import com.app.shared.dto.response.UserDTO;
import org.springframework.modulith.NamedInterface;

@NamedInterface
public interface UserGateway {
  UserDTO getUserByUsername(String username);

  UserDTO getUserById(Long id);
}
