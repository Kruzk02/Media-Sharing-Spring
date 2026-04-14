package com.app.module.comment.infrastructure.client;

import com.app.shared.dto.response.CommentDTO;
import com.app.shared.gateway.CommentGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CommentApiClient implements CommentGateway {

  private final RestClient restClient;

  @Autowired
  public CommentApiClient(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public CommentDTO getCommentById(Long commentId) {
    return restClient
        .get()
        .uri("/api/comments/{commentId}", commentId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(CommentDTO.class);
  }
}
