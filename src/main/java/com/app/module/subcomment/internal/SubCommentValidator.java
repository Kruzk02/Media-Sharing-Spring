package com.app.module.subcomment.internal;

import com.app.module.subcomment.application.exception.SubCommentIsEmptyException;
import org.springframework.web.multipart.MultipartFile;

public class SubCommentValidator {
  public static void validateContent(String content, MultipartFile media) {
    if ((content == null || content.trim().isEmpty()) && (media == null || media.isEmpty())) {
      throw new SubCommentIsEmptyException(
          "A comment must have either content or a media attachment.");
    }
  }
}
