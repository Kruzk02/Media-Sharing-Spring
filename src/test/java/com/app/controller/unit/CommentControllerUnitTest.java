package com.app.controller.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.app.controller.CommentController;
import com.app.model.Comment;
import com.app.model.DetailsType;
import com.app.model.Hashtag;
import com.app.model.Media;
import com.app.model.MediaType;
import com.app.model.SortType;
import com.app.model.SubComment;
import com.app.service.CommentService;
import com.app.service.SubCommentService;
import com.app.user.model.Gender;
import com.app.user.model.User;
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

  @Test
  void findAllSubCommentById_shouldThrow_whenLimitIsInvalid() {
    assertThrows(
        IllegalArgumentException.class,
        () -> commentController.findAllSubCommentById(1L, SortType.NEWEST, 0, 0));
  }

  @Test
  void findAllSubCommentById_shouldPassCorrectArguments() {
    var subComments =
        List.of(
            SubComment.builder()
                .content("content")
                .user(
                    User.builder()
                        .id(1L)
                        .username("username")
                        .email("email@gmail.com")
                        .password("HashedPassword")
                        .gender(Gender.MALE)
                        .media(Media.builder().id(1L).mediaType(MediaType.IMAGE).url("url").build())
                        .bio("bio")
                        .enable(false)
                        .build())
                .comment(
                    Comment.builder()
                        .id(1L)
                        .content("content123")
                        .pinId(1L)
                        .userId(1L)
                        .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
                        .mediaId(1L)
                        .build())
                .media(Media.builder().id(1L).url("url").mediaType(MediaType.IMAGE).build())
                .build());

    when(subCommentService.findAllByCommentId(eq(1L), eq(SortType.NEWEST), eq(10), eq(0)))
        .thenReturn(subComments);

    var response = commentController.findAllSubCommentById(1L, SortType.NEWEST, 10, 0);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }
}
