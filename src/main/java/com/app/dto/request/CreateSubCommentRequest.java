package com.app.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record CreateSubCommentRequest(String content, MultipartFile media, Long commentId) {}
