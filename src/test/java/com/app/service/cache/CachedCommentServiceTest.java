package com.app.service.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.app.module.comment.application.dto.request.CreateCommentRequest;
import com.app.module.comment.application.dto.request.UpdatedCommentRequest;
import com.app.module.comment.application.service.CachedCommentService;
import com.app.module.comment.application.service.CommentService;
import com.app.module.comment.domain.Comment;
import com.app.module.hashtag.domain.Hashtag;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CachedCommentServiceTest extends AbstractRedisTest<Comment> {

  private CachedCommentService cachedCommentService;
  private CommentService commentService;

  private Comment comment;

  @BeforeEach
  @Override
  void setUp() {
    super.setUp();

    commentService = mock(CommentService.class);
    cachedCommentService = new CachedCommentService(redisTemplate, commentService);

    comment =
        Comment.builder()
            .id(1L)
            .content("content123")
            .pinId(1L)
            .userId(1L)
            .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
            .build();
  }

  @Test
  @Order(1)
  void save() {
    when(commentService.save(new CreateCommentRequest("content123", 1L, null, Set.of("tag"))))
        .thenReturn(comment);

    Comment cached =
        cachedCommentService.save(new CreateCommentRequest("content123", 1L, null, Set.of("tag")));
    assertEquals(1L, cached.getId());
    assertEquals("content123", cached.getContent());
    assertEquals(1L, cached.getPinId());
    assertEquals(1L, cached.getUserId());
    assertEquals(List.of(Hashtag.builder().id(1L).tag("tag").build()), cached.getHashtags());

    verify(commentService).save(new CreateCommentRequest("content123", 1L, null, Set.of("tag")));
  }

  @Test
  @Order(2)
  void findById() {
    Comment cached = cachedCommentService.findById(1L, DetailsType.DETAIL);

    assertEquals(1L, cached.getId());
    assertEquals("content123", cached.getContent());
    assertEquals(1L, cached.getPinId());
    assertEquals(1L, cached.getUserId());
    assertEquals(List.of(Hashtag.builder().id(1L).tag("tag").build()), cached.getHashtags());
  }

  @Test
  @Order(3)
  void findByPinId() {
    List<Comment> comments = cachedCommentService.findByPinId(1L, SortType.NEWEST, 10, 0);
    assertTrue(comments.isEmpty());
  }

  @Test
  @Order(4)
  void findByHashtag() {
    List<Comment> comments = cachedCommentService.findByHashtag("tag", 10, 0);
    assertTrue(comments.isEmpty());
  }

  @Test
  @Order(5)
  void update() {
    when(commentService.update(1L, new UpdatedCommentRequest("content", null, Set.of("tag"))))
        .thenReturn(
            Comment.builder()
                .id(1L)
                .content("content")
                .pinId(1L)
                .userId(1L)
                .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
                .build());

    Comment cached =
        cachedCommentService.update(1L, new UpdatedCommentRequest("content", null, Set.of("tag")));
    assertEquals(1L, cached.getId());
    assertEquals("content", cached.getContent());
    assertEquals(1L, cached.getPinId());
    assertEquals(1L, cached.getUserId());
    assertEquals(List.of(Hashtag.builder().id(1L).tag("tag").build()), cached.getHashtags());

    verify(commentService).update(1L, new UpdatedCommentRequest("content", null, Set.of("tag")));
  }
}
