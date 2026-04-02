package com.app.module.pin.infrastructure.client;

import com.app.shared.dto.response.PinDTO;
import com.app.shared.gateway.PinGateway;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PinApiClient implements PinGateway {
  private final RestClient restClient;

  public PinApiClient(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public List<PinDTO> getPinsByIds(List<Long> ids) {
    return restClient
        .post()
        .uri("/api/pin/by-ids")
        .accept(MediaType.APPLICATION_JSON)
        .body(ids)
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});
  }
}
