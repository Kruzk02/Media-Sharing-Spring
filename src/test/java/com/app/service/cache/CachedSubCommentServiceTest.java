package com.app.service.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.app.module.comment.application.dto.request.UpdatedCommentRequest;
import com.app.module.comment.domain.Comment;
import com.app.module.subcomment.application.dto.CreateSubCommentRequest;
import com.app.module.subcomment.application.service.CachedSubCommentService;
import com.app.module.subcomment.application.service.SubCommentService;
import com.app.module.subcomment.domain.SubComment;
import com.app.module.user.domain.entity.User;
import com.app.module.user.domain.status.Gender;
import com.app.shared.type.SortType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CachedSubCommentServiceTest extends AbstractRedisTest<SubComment> {

  private CachedSubCommentService cachedService;
  private SubCommentService mockSubCommentService;

  private SubComment subComment;

  @BeforeEach
  @Override
  void setUp() {
    super.setUp();

    mockSubCommentService = mock(SubCommentService.class);
    cachedService = new CachedSubCommentService(redisTemplate, mockSubCommentService);

    subComment =
        SubComment.builder()
            .id(1L)
            .comment(
                Comment.builder()
                    .id(1L)
                    .mediaId(1L)
                    .pinId(1L)
                    .hashtags(Collections.emptyList())
                    .userId(1L)
                    .build())
            .content("content")
            .user(
                User.builder()
                    .id(1L)
                    .username("username3")
                    .email("email3@gmail.com")
                    .password("HashedPassword")
                    .gender(Gender.MALE)
                    .mediaId(1L)
                    .bio("bio")
                    .enable(false)
                    .build())
            .build();
  }

  @Test
  @Order(1)
  void save() {
    when(mockSubCommentService.save(new CreateSubCommentRequest("content", null, 1L)))
        .thenReturn(subComment);

    SubComment saved = cachedService.save(new CreateSubCommentRequest("content", null, 1L));
    assertEquals(1L, saved.getId());
    assertEquals("content", saved.getContent());

    verify(mockSubCommentService).save(new CreateSubCommentRequest("content", null, 1L));
  }

  @Test
  @Order(2)
  void findById() {
    SubComment cached = cachedService.findById(1L);
    assertEquals(1L, cached.getId());
    assertEquals("content", cached.getContent());
  }

  @Test
  @Order(3)
  void findAllByCommentId() {
    List<SubComment> subComments = cachedService.findAllByCommentId(1L, SortType.NEWEST, 10, 0);
    assertTrue(subComments.isEmpty());
  }

  @Test
  @Order(4)
  void update() {
    when(mockSubCommentService.update(1L, new UpdatedCommentRequest("Content", null, null)))
        .thenReturn(
            SubComment.builder()
                .id(1L)
                .comment(
                    Comment.builder()
                        .id(1L)
                        .mediaId(1L)
                        .pinId(1L)
                        .hashtags(Collections.emptyList())
                        .userId(1L)
                        .build())
                .content("Content")
                .user(
                    User.builder()
                        .id(1L)
                        .username("username3")
                        .email("email3@gmail.com")
                        .password("HashedPassword")
                        .gender(Gender.MALE)
                        .mediaId(1L)
                        .bio("bio")
                        .enable(false)
                        .build())
                .build());

    SubComment updated = cachedService.update(1L, new UpdatedCommentRequest("Content", null, null));

    assertEquals(1L, updated.getId());
    assertEquals("Content", updated.getContent());

    verify(mockSubCommentService).update(1L, new UpdatedCommentRequest("Content", null, null));
  }
}
