package com.app.comment.controller;

import com.app.comment.dto.request.CreateCommentRequest;
import com.app.comment.dto.request.UpdatedCommentRequest;
import com.app.comment.dto.response.CommentResponse;
import com.app.comment.model.Comment;
import com.app.comment.service.CommentService;
import com.app.model.DetailsType;
import com.app.model.SortType;
import com.app.subcomment.dto.SubCommentResponse;
import com.app.subcomment.model.SubComment;
import com.app.subcomment.service.SubCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
public class CommentController {

  private final CommentService commentService;
  private final SubCommentService subCommentService;

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

  @Operation(summary = "Fetch all sub comments by comment id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully fetch all sub comments",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SubComment.class))
            }),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @GetMapping("/{id}/sub-comments")
  public ResponseEntity<List<SubCommentResponse>> findAllSubCommentById(
      @Parameter(description = "Comment Id of the sub comments to be searched") @PathVariable
          Long id,
      @Parameter(description = "Sorting type for sub comments: NEWEST, OLDEST or DEFAULT")
          @RequestParam(defaultValue = "NEWEST")
          SortType sortType,
      @Parameter(description = "Maximum number of sub comments to be retrieved")
          @RequestParam(defaultValue = "10")
          int limit,
      @Parameter(description = "Offset for pagination, indicating the starting point")
          @RequestParam(defaultValue = "0")
          int offset) {
    if (limit <= 0 || offset < 0) {
      throw new IllegalArgumentException(
          "Limit must be greater than 0 and offset must be non-negative.");
    }

    List<SubCommentResponse> subComments =
        subCommentService.findAllByCommentId(id, sortType, limit, offset).stream()
            .sorted(Comparator.comparing(SubComment::getCreateAt))
            .map(SubCommentResponse::fromEntity)
            .toList();

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(subComments);
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
                comment.getHashtags().stream().toList()));
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
                comment.getHashtags().stream().toList()));
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
