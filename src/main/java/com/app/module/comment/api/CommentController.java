package com.app.module.comment.api;

import com.app.module.comment.application.dto.request.CreateCommentRequest;
import com.app.module.comment.application.dto.request.UpdatedCommentRequest;
import com.app.module.comment.application.dto.response.CommentResponse;
import com.app.module.comment.application.service.CommentService;
import com.app.module.comment.domain.Comment;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @Operation(description = "Get comment details by its ID ")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Comment found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = CommentResponse.class))
            }),
        @ApiResponse(responseCode = "404", description = "Comment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @GetMapping("/{id}")
  public ResponseEntity<CommentResponse> findById(
      @PathVariable Long id, @RequestParam(defaultValue = "basic") String view) {
    Comment comment;

    if ("details".equalsIgnoreCase(view)) {
      comment = commentService.findById(id, DetailsType.DETAIL);
    } else {
      comment = commentService.findById(id, DetailsType.BASIC);
    }

    if (comment == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .contentType(MediaType.APPLICATION_JSON)
          .build();
    }

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getPinId(),
                comment.getUserId(),
                comment.getMediaId(),
                comment.getCreated_at(),
                view.equalsIgnoreCase("details")
                    ? new ArrayList<>(comment.getHashtags())
                    : new ArrayList<>()));
  }

  @GetMapping(value = "/of-pin/{id}/sse-comment", produces = "text/event-stream")
  public SseEmitter stream(@PathVariable long id) {
    return commentService.createEmitter(id);
  }

  @Operation(summary = "Find all comment by neither pinId or hashtag")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully get all comment",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Comment.class))),
        @ApiResponse(responseCode = "404", description = "Pin not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @GetMapping()
  public ResponseEntity<List<CommentResponse>> getAllComment(
      @Parameter(description = "id of the pin whose comment are to be retrieved", required = false)
          @RequestParam(required = false)
          Long pinId,
      @Parameter(description = "tag of the comments") @RequestParam(required = false) String tag,
      @Parameter(description = "Sorting type for comments: NEWEST, OLDEST")
          @RequestParam(defaultValue = "NEWEST", required = false)
          SortType sortType,
      @Parameter(description = "Maximum number of comments to be retrieved")
          @RequestParam(defaultValue = "10")
          int limit,
      @Parameter(description = "Offset for pagination, indicating the starting point")
          @RequestParam(defaultValue = "0")
          int offset) {
    if (limit <= 0 || offset < 0) {
      throw new IllegalArgumentException(
          "Limit must be greater than 0 and offset must be non-negative.");
    }

    if (pinId != null && tag != null) {
      throw new IllegalArgumentException("Cannot filter by pinId and tag at the same time");
    }

    if (pinId == null && tag == null) {
      throw new IllegalArgumentException("Either pinId or tag must be provided");
    }

    List<Comment> comments;
    if (pinId != null) {
      comments = commentService.findByPinId(pinId, sortType, limit, offset);
    } else {
      comments = commentService.findByHashtag(tag, limit, offset);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(comments.stream().map(CommentResponse::fromEntity).toList());
  }

  @Operation(description = "create an comment")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Create an comment",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = CommentResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PostMapping
  public ResponseEntity<CommentResponse> create(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Comment to created",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = Comment.class)))
          @ModelAttribute
          CreateCommentRequest request) {
    Comment comment = commentService.save(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getPinId(),
                comment.getUserId(),
                comment.getMediaId(),
                comment.getCreated_at(),
                new ArrayList<>()));
  }

  @Operation(description = "Update an comment")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Success update an comment",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = CommentResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PutMapping("/{id}")
  public ResponseEntity<CommentResponse> update(
      @PathVariable Long id, @ModelAttribute UpdatedCommentRequest request) {
    Comment comment = commentService.update(id, request);
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getPinId(),
                comment.getUserId(),
                comment.getMediaId(),
                comment.getCreated_at(),
                new ArrayList<>()));
  }

  @Operation(summary = "Delete a comment by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Comment successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Comment not found")
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "Id of the comment deleted") @PathVariable Long id) {
    commentService.deleteById(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .contentType(MediaType.APPLICATION_JSON)
        .build();
  }
}
