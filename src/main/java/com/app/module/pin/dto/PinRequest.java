package com.app.module.pin.dto;

import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public record PinRequest(String description, MultipartFile file, Set<String> hashtags) {}
