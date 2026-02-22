package com.app.module.pin.application.dto;

import com.app.module.pin.domain.Pin;
import java.util.List;

public record PinKeysetResponse(List<Pin> pins, String cursor) {}
