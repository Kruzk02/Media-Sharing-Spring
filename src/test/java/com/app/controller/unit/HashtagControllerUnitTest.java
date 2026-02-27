package com.app.controller.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.app.module.comment.application.service.CommentService;
import com.app.module.comment.domain.Comment;
import com.app.module.hashtag.api.HashtagController;
import com.app.module.hashtag.domain.Hashtag;
import com.app.module.pin.application.service.PinService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class HashtagControllerUnitTest {
  @Mock private CommentService commentService;
  @Mock private PinService pinService;

  @InjectMocks private HashtagController hashtagController;

  @Test
  void getAllCommentByTag_shouldThrow_whenLimitIsValid() {
    assertThrows(
        IllegalArgumentException.class, () -> hashtagController.getAllCommentByTag("tag", 0, 0));
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

    var response = hashtagController.getAllCommentByTag("tag", 10, 0);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
  }
}
