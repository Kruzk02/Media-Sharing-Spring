package com.app.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record UpdatedMediaRequest(MultipartFile file) {}
