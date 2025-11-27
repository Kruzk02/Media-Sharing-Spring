package com.app.module.subcomment.application.dto;

import org.springframework.web.multipart.MultipartFile;

public record CreateSubCommentRequest(String content, MultipartFile media, Long commentId) {}
