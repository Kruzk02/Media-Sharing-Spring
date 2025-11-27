package com.app.controller.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.app.module.hashtag.model.Hashtag;
import com.app.module.pin.api.PinController;
import com.app.module.pin.application.dto.PinResponse;
import com.app.module.pin.application.service.PinService;
import com.app.module.pin.domain.Pin;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PinControllerUnitTest {

  @Mock private PinService pinService;

  @InjectMocks private PinController pinController;

  @Test
  void getAllPins_ShouldThrow_WhenLimitIsInvalid() {
    assertThrows(
        IllegalArgumentException.class, () -> pinController.getAllPins(SortType.NEWEST, 0, 0));
  }

  @Test
  void getAllPins_ShouldPassCorrectArguments() {
    List<Pin> pins =
        List.of(Pin.builder().id(1L).userId(1L).mediaId(1L).description("Hello World").build());

    when(pinService.getAllPins(eq(SortType.NEWEST), eq(10), eq(0))).thenReturn(pins);

    ResponseEntity<List<PinResponse>> response = pinController.getAllPins(SortType.NEWEST, 10, 0);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
  }

  @Test
  void getPinById_ShouldPassCorrectBasicPin() {
    var pin = Pin.builder().id(1L).userId(1L).mediaId(1L).description("Hello World").build();

    when(pinService.findById(eq(1L), eq(DetailsType.BASIC))).thenReturn(pin);

    var response = pinController.getPinById(1L, "basic");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().id());
    assertEquals(1L, response.getBody().userId());
    assertEquals(1L, response.getBody().mediaId());
    assertEquals("Hello World", response.getBody().description());
  }

  @Test
  void getPinById_ShouldPassCorrectDetailPin() {
    var pin =
        Pin.builder()
            .id(1L)
            .userId(1L)
            .mediaId(1L)
            .description("Hello World")
            .hashtags(List.of(Hashtag.builder().id(1L).tag("tag").build()))
            .build();

    when(pinService.findById(eq(1L), eq(DetailsType.DETAIL))).thenReturn(pin);

    var response = pinController.getPinById(1L, "detail");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().id());
    assertEquals(1L, response.getBody().userId());
    assertEquals(1L, response.getBody().mediaId());
    assertEquals("Hello World", response.getBody().description());
    assertEquals(List.of(Hashtag.builder().id(1L).tag("tag").build()), response.getBody().tag());
  }
}
