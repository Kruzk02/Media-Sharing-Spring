package com.app.module.pin.infrastructure.client;

import com.app.module.pin.infrastructure.dto.UserDto;
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
  public UserDto getUserByUsername(String username) {
    return restClient
        .get()
        .uri("/api/users/{username}/info", username)
        .retrieve()
        .body(UserDto.class);
  }
}
