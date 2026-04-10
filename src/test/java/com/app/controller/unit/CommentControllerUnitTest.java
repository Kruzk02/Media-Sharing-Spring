package com.app.controller.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.app.module.comment.api.CommentController;
import com.app.module.comment.application.service.CommentService;
import com.app.module.comment.domain.Comment;
import com.app.module.hashtag.domain.Hashtag;
import com.app.module.subcomment.application.service.SubCommentService;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CommentControllerUnitTest {
  @Mock private CommentService commentService;
  @Mock private SubCommentService subCommentService;

  @InjectMocks private CommentController commentController;

  @Test
  void getAllCommentByTag_shouldThrow_whenLimitIsValid() {
    assertThrows(
        IllegalArgumentException.class,
        () -> commentController.getAllComment(null, "tag", SortType.NEWEST, 0, 0));
  }

  @Test
  void getAllCommentByTag_shouldPassCorrectArguments() {
    List<Comment> comments =
        List.of(
            Comment.builder()
                .id(1L)
                .content("content")
                .userId(1L)
                .mediaId(1L)
                .pinId(1L)
                .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
                .build());

    when(commentService.findByHashtag(eq("tag"), eq(10), eq(0))).thenReturn(comments);

    var response = commentController.getAllComment(null, "tag", SortType.NEWEST, 10, 0);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
  }

  @Test
  void findById_shouldPassCorrectBasicComment() {
    var comment =
        Comment.builder()
            .id(1L)
            .content("content")
            .userId(1L)
            .mediaId(1L)
            .pinId(1L)
            .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
            .build();

    when(commentService.findById(eq(1L), eq(DetailsType.BASIC))).thenReturn(comment);

    var response = commentController.findById(1L, "basic");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().id());
    assertEquals(1L, response.getBody().userId());
    assertEquals(1L, response.getBody().mediaId());
    assertEquals("content", response.getBody().content());
    assertEquals(List.of(), response.getBody().tag());
  }

  @Test
  void findById_shouldPassCorrectDetailComment() {
    var comment =
        Comment.builder()
            .id(1L)
            .content("content")
            .userId(1L)
            .mediaId(1L)
            .pinId(1L)
            .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
            .build();

    when(commentService.findById(eq(1L), eq(DetailsType.DETAIL))).thenReturn(comment);

    var response = commentController.findById(1L, "details");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().id());
    assertEquals(1L, response.getBody().userId());
    assertEquals(1L, response.getBody().mediaId());
    assertEquals("content", response.getBody().content());
    assertEquals(new ArrayList<>(comment.getHashtags()), response.getBody().tag());
  }
}
