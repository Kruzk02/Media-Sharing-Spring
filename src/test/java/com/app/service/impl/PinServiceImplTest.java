package com.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.app.module.hashtag.domain.Hashtag;
import com.app.module.hashtag.infrastructure.HashtagDao;
import com.app.module.pin.application.dto.PinRequest;
import com.app.module.pin.application.service.PinServiceImpl;
import com.app.module.pin.domain.Pin;
import com.app.module.pin.infrastructure.PinDao;
import com.app.module.user.domain.entity.User;
import com.app.module.user.domain.status.Gender;
import com.app.module.user.infrastructure.user.UserDao;
import com.app.shared.exception.sub.PinNotFoundException;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class PinServiceImplTest {

  @Mock private PinDao pinDao;
  @Mock private UserDao userDao;
  @Mock private HashtagDao hashtagDao;
  @Mock private MultipartFile mockFile;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private PinServiceImpl pinService;

  private Pin pin;
  private User user;

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
  void getAllPins_shouldReturnListOfPin() {
    Mockito.when(pinDao.getAllPins(SortType.NEWEST, 10, 0)).thenReturn(List.of(pin));
    var result = pinService.getAllPins(SortType.NEWEST, 10, 0);

    assertNotNull(result);
    assertEquals(List.of(pin), result);
  }

  @Test
  void getAllPinsByHashTag_shouldReturnListOfPin() {
    Mockito.when(pinDao.getAllPinsByHashtag("tag", 10, 0)).thenReturn(List.of(pin));
    var result = pinService.getAllPinsByHashtag("tag", 10, 0);

    assertNotNull(result);
    assertEquals(List.of(pin), result);
  }

  @Test
  void save_shouldSavePinSuccessfully() {

    Authentication auth = Mockito.mock(Authentication.class);
    Mockito.when(auth.getName()).thenReturn("username");
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
    Mockito.when(userDao.findUserByUsername("username")).thenReturn(user);

    PinRequest request = new PinRequest("Description", mockFile, Set.of("tag1", "tag2"));
    Mockito.when(hashtagDao.findByTag(Set.of("tag1", "tag2"))).thenReturn(new HashMap<>());
    Mockito.when(hashtagDao.save(Mockito.argThat(ht -> ht.getTag() != null)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Mockito.when(
            pinDao.save(
                Mockito.argThat(
                    p ->
                        !p.getHashtags().isEmpty()
                            && p.getDescription() != null
                            && p.getUserId() != 0)))
        .thenReturn(pin);

    Pin result = pinService.save(request);

    assertNotNull(result);
    Mockito.verify(pinDao)
        .save(
            Mockito.argThat(
                p ->
                    !p.getHashtags().isEmpty()
                        && p.getDescription() != null
                        && p.getUserId() != 0));
  }

  @Test
  void update_ShouldUpdatePin_WhenValidRequestAndMatchingUser() {
    Mockito.when(pinDao.findById(1L, DetailsType.BASIC)).thenReturn(pin);
    Mockito.when(userDao.findUserByUsername("username")).thenReturn(user);

    PinRequest pinRequest = new PinRequest("New description", mockFile, Set.of("tag1"));

    Mockito.when(hashtagDao.findByTag(Set.of("tag1"))).thenReturn(Map.of());
    Mockito.when(hashtagDao.save(Mockito.argThat(ht -> ht.getTag() != null)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Mockito.when(
            pinDao.update(
                Mockito.eq(1L),
                Mockito.argThat(
                    p ->
                        !p.getHashtags().isEmpty()
                            && p.getDescription() != null
                            && p.getUserId() != 0)))
        .thenAnswer(invocation -> invocation.getArgument(1));

    Pin updatedPin = pinService.update(1L, pinRequest);

    assertEquals("New description", updatedPin.getDescription());
    assertEquals(user.getId(), updatedPin.getUserId());

    Mockito.verify(pinDao)
        .update(
            Mockito.eq(1L),
            Mockito.argThat(
                p ->
                    p.getId() != null
                        && !p.getHashtags().isEmpty()
                        && p.getDescription() != null
                        && p.getUserId() != 0));
    Mockito.verify(hashtagDao).save(Mockito.argThat(ht -> ht.getTag() != null));
  }

  @Test
  void findById_shouldReturnPin() {
    Mockito.when(pinDao.findById(1L, DetailsType.BASIC)).thenReturn(pin);
    var result = pinService.findById(1L, DetailsType.BASIC);

    assertNotNull(result);
    assertEquals(pin, result);
  }

  @Test
  void findPinByUserId_shouldReturnListOfPin() {
    Mockito.when(pinDao.findPinByUserId(1L, 10, 0)).thenReturn(List.of(pin));
    var result = pinService.findPinByUserId(1L, 10, 0);

    assertNotNull(result);
    assertEquals(List.of(pin), result);
  }

  @Test
  void deleteById_shouldDeleteExistingPin() throws IOException {
    Mockito.when(userDao.findUserByUsername("username")).thenReturn(user);
    Mockito.when(pinDao.findById(1L, DetailsType.BASIC)).thenReturn(pin);

    pinService.delete(1L);

    Mockito.verify(pinDao).deleteById(1L);
  }

  @Test
  void testDeleteById_PinNotFound() {
    Mockito.when(pinDao.findById(2L, DetailsType.BASIC)).thenReturn(null);

    PinNotFoundException ex = assertThrows(PinNotFoundException.class, () -> pinService.delete(2L));
    assertEquals("Pin not found with a id: 2", ex.getMessage());
  }
}
