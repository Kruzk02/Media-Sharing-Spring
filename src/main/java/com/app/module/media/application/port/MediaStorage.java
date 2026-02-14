package com.app.module.media.application.port;

import com.app.module.media.application.dto.MediaInfo;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.multipart.MultipartFile;

public interface MediaStorage {
  CompletableFuture<Void> save(MultipartFile file, MediaInfo mediaInfo);

  CompletableFuture<Void> delete(MediaInfo mediaInfo);
}
