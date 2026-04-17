package com.app.module.pin.api;

import com.app.module.pin.application.dto.PinRequest;
import com.app.module.pin.application.dto.PinResponse;
import com.app.module.pin.application.service.PinService;
import com.app.module.pin.domain.Pin;
import com.app.shared.dto.response.CursorPage;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pin")
@AllArgsConstructor
public class PinController {

  private final PinService pinService;

  @Operation(summary = "Get all Pins")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully get all pins",
            content = {
              @Content(mediaType = "application/json", schema = @Schema(implementation = Pin.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json"))
      })
  @GetMapping
  public ResponseEntity<CursorPage<Pin>> getPins(
      @Parameter(description = "Sorting type for pins: NEWEST, OLDEST or DEFAULT")
          @RequestParam(defaultValue = "NEWEST", required = false)
          SortType sortType,
      @Parameter(description = "Maximum number of pins to be retrieved")
          @RequestParam(defaultValue = "25")
          int limit,
      @Parameter(
              description =
                  "Opaque pagination cursor returned by a previous request. Do not modify this value. Pass it as-is to retrieve the next page.",
              example = "AAABnJ5uOzwAAAAAAAAAew")
          @RequestParam(required = false)
          String cursor,
      @Parameter(description = "tag of the pin") @RequestParam(required = false) String tag,
      @Parameter(description = "User id of the pin") @RequestParam(required = false) Long userId) {
    if (limit <= 0) {
      throw new IllegalArgumentException("Limit must be greater than 0.");
    }
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            userId != null && userId != 0
                ? pinService.findPinByUserId(userId, limit, cursor)
                : tag != null && !tag.isBlank()
                    ? pinService.getAllPinsByHashtag(tag, limit, cursor)
                    : pinService.getAllPins(sortType, limit, cursor));
  }

  @Operation(summary = "Upload a pin")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Pin uploaded successfully",
            content = {
              @Content(mediaType = "application/json", schema = @Schema(implementation = Pin.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "413",
            description = "File is larger than 10MB",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "415",
            description = "File type is not an image",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json"))
      })
  @PostMapping("/upload")
  public ResponseEntity<PinResponse> upload(@ModelAttribute PinRequest request) {
    Pin pin = pinService.save(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new PinResponse(
                pin.getId(),
                pin.getUserId(),
                pin.getDescription(),
                pin.getMediaId(),
                Collections.emptyList(),
                pin.getCreatedAt()));
  }

  @Operation(description = "Update an existing pin")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Success update an pin",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = PinResponse.class))
            }),
        @ApiResponse(responseCode = "404", description = "Pin not found"),
        @ApiResponse(responseCode = "400", description = "Invalid Input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PutMapping("/{id}")
  public ResponseEntity<PinResponse> update(
      @PathVariable Long id, @ModelAttribute PinRequest request) {
    Pin pin = pinService.update(id, request);
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new PinResponse(
                pin.getId(),
                pin.getUserId(),
                pin.getDescription(),
                pin.getMediaId(),
                new ArrayList<>(),
                pin.getCreatedAt()));
  }

  @Operation(summary = "Fetch a basic pin detail by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the pin",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Pin.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Pin not found",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @GetMapping("/{id}")
  public ResponseEntity<PinResponse> getPinById(
      @Parameter(description = "id of the pin to be searched", required = true) @PathVariable
          Long id,
      @RequestParam(defaultValue = "basic") String view) {
    Pin pin;
    if ("detail".equalsIgnoreCase(view)) {
      pin = pinService.findById(id, DetailsType.DETAIL);
    } else {
      pin = pinService.findById(id, DetailsType.BASIC);
    }

    if (pin == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .contentType(MediaType.APPLICATION_JSON)
          .build();
    }

    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            new PinResponse(
                pin.getId(),
                pin.getUserId(),
                pin.getDescription(),
                pin.getMediaId(),
                view.equalsIgnoreCase("detail") && pin.getHashtags() != null
                    ? new ArrayList<>(pin.getHashtags())
                    : new ArrayList<>(),
                pin.getCreatedAt()));
  }

  @Operation(summary = "Fetch a multiple pin its ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the pin",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PostMapping("/by-ids")
  public ResponseEntity<List<PinResponse>> getPinByIds(@RequestBody List<Long> ids) {
    List<PinResponse> pins =
        pinService.findByIdIn(ids).stream().map(PinResponse::fromEntity).toList();
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(pins);
  }

  @Operation(summary = "Delete an pin by its id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Success delete an ebook",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(
            responseCode = "404",
            description = "Ebook not found",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json"))
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePinById(@PathVariable Long id) throws IOException {
    pinService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .contentType(MediaType.APPLICATION_JSON)
        .build();
  }
}
