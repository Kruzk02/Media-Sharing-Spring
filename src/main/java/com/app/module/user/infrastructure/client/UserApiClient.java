package com.app.module.user.infrastructure.client;

import com.app.shared.dto.response.UserDTO;
import com.app.shared.gateway.UserGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserApiClient implements UserGateway {

  private final RestClient restClient;

  @Autowired
  public UserApiClient(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public UserDTO getUserByUsername(String username) {
    return restClient
        .get()
        .uri("/api/users/info/username/{username}", username)
        .retrieve()
        .body(UserDTO.class);
  }

  @Override
  public UserDTO getUserById(Long id) {
    return restClient.get().uri("/api/users/info/id/{id}", id).retrieve().body(UserDTO.class);
  }
}
