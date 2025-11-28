package com.app.module.comment.dto.response;

import com.app.module.comment.model.Comment;
import com.app.module.hashtag.domain.Hashtag;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Details of a single comment")
public record CommentResponse(
    @Schema(description = "Id of the comment", example = "123") Long id,
    @Schema(description = "Content of the comment", example = "HELLO WORLD") String content,
    @Schema(description = "Id of the pin associated with comment", example = "123") long pinId,
    @Schema(description = "Id of the user associated with comment", example = "123") long userId,
    @Schema(description = "Id of the media associated with comment", example = "123") long mediaId,
    LocalDateTime created_at,
    List<Hashtag> tag) {

  public static CommentResponse fromEntity(Comment comment) {
    return new CommentResponse(
        comment.getId(),
        comment.getContent(),
        comment.getPinId(),
        comment.getUserId(),
        comment.getMediaId(),
        comment.getCreated_at(),
        comment.getHashtags() == null ? new ArrayList<>() : List.copyOf(comment.getHashtags()));
  }
}
