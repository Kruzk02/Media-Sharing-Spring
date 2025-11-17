package com.app.shared.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record UpdatedMediaRequest(MultipartFile file) {}
