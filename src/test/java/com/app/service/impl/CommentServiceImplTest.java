package com.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.module.comment.application.dto.request.CreateCommentRequest;
import com.app.module.comment.application.dto.request.UpdatedCommentRequest;
import com.app.module.comment.application.service.CommentServiceImpl;
import com.app.module.comment.domain.Comment;
import com.app.module.comment.infrastructure.CommentDao;
import com.app.module.hashtag.domain.Hashtag;
import com.app.module.hashtag.infrastructure.HashtagDao;
import com.app.module.media.domain.entity.Media;
import com.app.module.notification.domain.Notification;
import com.app.module.pin.domain.Pin;
import com.app.module.pin.infrastructure.PinDao;
import com.app.module.user.domain.entity.User;
import com.app.module.user.domain.status.Gender;
import com.app.module.user.infrastructure.user.UserDao;
import com.app.shared.event.comment.delete.DeleteCommentMediaEvent;
import com.app.shared.event.comment.save.SaveCommentMediaEvent;
import com.app.shared.event.comment.update.UpdateCommentMediaEvent;
import com.app.shared.message.producer.NotificationEventProducer;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

  @Mock private CommentDao commentDao;
  @Mock private UserDao userDao;
  @Mock private PinDao pinDao;
  @Mock private HashtagDao hashtagDao;
  @Mock private Map<Long, SseEmitter> emitters;
  @Mock private NotificationEventProducer notificationEventProducer;
  @Mock private MultipartFile mockFile;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private CommentServiceImpl commentService;

  private Comment comment;
  private User user;
  private Media media;
  private Pin pin;

  @BeforeEach
  void setUp() {
    Hashtag hashtag = Hashtag.builder().id(1L).tag("tag").build();
    user =
        User.builder()
            .id(1L)
            .username("username")
            .email("email@gmail.com")
            .password("encodedPassword")
            .enable(false)
            .gender(Gender.MALE)
            .build();
    comment =
        Comment.builder()
            .id(1L)
            .pinId(1L)
            .userId(1L)
            .mediaId(1L)
            .content("content")
            .hashtags(List.of(hashtag))
            .build();

    pin =
        Pin.builder()
            .id(1L)
            .description("description")
            .userId(1L)
            .mediaId(1L)
            .hashtags(List.of(hashtag))
            .build();
  }

  @Test
  void save_shouldSavedCommentSuccessfully() {
    Authentication auth = Mockito.mock(Authentication.class);
    Mockito.when(auth.getName()).thenReturn("username");
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
    Mockito.when(userDao.findUserByUsername("username")).thenReturn(user);

    var request = new CreateCommentRequest("content", 1L, mockFile, Set.of("tag1", "tag2"));

    Mockito.when(pinDao.findById(1L, DetailsType.BASIC)).thenReturn(pin);

    Mockito.when(
            commentDao.save(
                Mockito.argThat(
                    c -> c.getContent() != null && c.getUserId() != 0 && c.getPinId() != 0)))
        .thenReturn(comment);

    var result = commentService.save(request);

    assertNotNull(result);
    assertEquals(comment.getId(), result.getId());
    Mockito.verify(commentDao)
        .save(
            Mockito.argThat(
                c -> c.getContent() != null && c.getUserId() != 0 && c.getPinId() != 0));

    Mockito.verify(notificationEventProducer).send(Mockito.any(Notification.class));
    Mockito.verify(eventPublisher).publishEvent(Mockito.any(SaveCommentMediaEvent.class));
  }

  @Test
  void update_shouldUpdateComment_whenValidRequestAndMatchUser() {

    Mockito.when(commentDao.findById(1L, DetailsType.BASIC)).thenReturn(comment);
    Mockito.when(userDao.findUserByUsername("username")).thenReturn(user);

    var request = new UpdatedCommentRequest("content", mockFile, Set.of("tag1", "tag2"));

    Mockito.when(
            commentDao.update(
                Mockito.eq(1L),
                Mockito.argThat(
                    c ->
                        c.getContent() != null
                            && !c.getHashtags().isEmpty()
                            && c.getUserId() != 0
                            && c.getPinId() != 0)))
        .thenAnswer(invocation -> invocation.getArgument(1));

    var result = commentService.update(1L, request);
    assertNotNull(result);
    assertEquals(comment.getId(), result.getId());

    Mockito.verify(commentDao)
        .update(
            Mockito.eq(1L),
            Mockito.argThat(
                c -> c.getContent() != null && c.getUserId() != 0 && c.getPinId() != 0));

    Mockito.verify(emitters).get(result.getId());
    Mockito.verify(eventPublisher).publishEvent(Mockito.any(UpdateCommentMediaEvent.class));
  }

  @Test
  void findById_shouldReturnComment() {
    Mockito.when(commentDao.findById(1L, DetailsType.DETAIL)).thenReturn(comment);
    var result = commentService.findById(1L, DetailsType.DETAIL);

    assertNotNull(result);
    assertEquals(comment, result);
  }

  @Test
  void findByPinId_shouldReturnListOfComment() {
    Mockito.when(commentDao.findByPinId(1L, SortType.NEWEST, 10, 0)).thenReturn(List.of(comment));
    var result = commentService.findByPinId(1L, SortType.NEWEST, 10, 0);

    assertNotNull(result);
    assertEquals(List.of(comment), result);
  }

  @Test
  void findByHashTag_shouldReturnListOfComment() {
    Mockito.when(commentDao.findByHashtag("tag", 10, 0)).thenReturn(List.of(comment));
    var result = commentService.findByHashtag("tag", 10, 0);

    assertNotNull(result);
    assertEquals(List.of(comment), result);
  }

  @Test
  void deleteById_shouldDeleteExistingComment() {
    Authentication auth = Mockito.mock(Authentication.class);
    Mockito.when(auth.getName()).thenReturn("username");
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
    Mockito.when(userDao.findUserByUsername("username")).thenReturn(user);

    Mockito.when(commentDao.findById(1L, DetailsType.BASIC)).thenReturn(comment);

    commentService.deleteById(1L);

    Mockito.verify(commentDao).deleteById(1L);
    Mockito.verify(eventPublisher).publishEvent(Mockito.any(DeleteCommentMediaEvent.class));
  }
}
