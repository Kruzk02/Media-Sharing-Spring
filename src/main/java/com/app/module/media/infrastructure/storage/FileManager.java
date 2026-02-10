package com.app.module.media.infrastructure.storage;

import com.app.module.media.application.dto.MediaInfo;
import com.app.module.media.domain.status.MediaType;
import com.app.shared.exception.sub.FileDeleteException;
import com.app.shared.exception.sub.FileSaveException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
public class FileManager {

  /**
   * Resolves the appropriate directory {@link Path} for a given file extension.
   *
   * <p>This method determines the {@link MediaType} associated with the provided extension and maps
   * it to a corresponding folder on the filesystem (e.g. "image" or "video"). If the directory does
   * not already exist, it will be created.
   *
   * @param extension the file extension (e.g. "jpg", "png", "mp4")
   * @return the {@link Path} representing the directory for this media type
   * @throws IOException if the directory cannot be created
   */
  private static Path resolveMediaType(String extension) throws IOException {
    Path path =
        switch (MediaType.fromExtension(extension)) {
          case IMAGE -> Paths.get("image");
          case VIDEO -> Paths.get("video");
        };
    if (Files.exists(path)) {
      Files.createDirectories(path);
    }
    return path;
  }

  /**
   * Saves the provided file to the appropriate directory based on its extension.
   *
   * @param file The file to be saved
   * @param mediaInfo contains the name of the file and extension to be saved.
   * @return A CompletableFuture that runs the save operation asynchronously.
   * @throws IllegalArgumentException If any of the input parameters are null.
   */
  public static CompletableFuture<Void> save(MultipartFile file, MediaInfo mediaInfo) {
    if (file == null || mediaInfo.filename() == null || mediaInfo.extension() == null) {
      throw new IllegalArgumentException("File, filename, and extension must not be null.");
    }

    return CompletableFuture.runAsync(
        () -> {
          try {
            Path folder = resolveMediaType(mediaInfo.extension());
            Path safeName = Paths.get(mediaInfo.filename()).getFileName();
            Path filePath = folder.resolve(safeName);

            try (InputStream inputStream = file.getInputStream()) {
              Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
              log.info("File saved successfully to path: {}", filePath);
            }
          } catch (IOException e) {
            log.error("Error saving media file", e);
            throw new CompletionException(
                new FileSaveException("Error saving media file: " + mediaInfo.filename(), e));
          }
        });
  }

  /**
   * Deletes the file with the given filename from the appropriate directory based on its extension.
   *
   * @param mediaInfo contains the name of the file and extension to be deleted.
   * @return A CompletableFuture that runs the delete operation asynchronously.
   * @throws IllegalArgumentException If any of the input parameters are null.
   */
  public static CompletableFuture<Void> delete(MediaInfo mediaInfo) {
    if (mediaInfo.filename() == null || mediaInfo.extension() == null) {
      throw new IllegalArgumentException("File, filename, and extension must not be null.");
    }

    return CompletableFuture.runAsync(
        () -> {
          try {
            Path folder = resolveMediaType(mediaInfo.extension());
            Path safeName = Paths.get(mediaInfo.filename()).getFileName();
            Path filePath = folder.resolve(safeName);

            if (Files.exists(filePath)) {
              Files.delete(filePath);
              log.info("File deleted successfully: {}", filePath);
            } else {
              log.warn("File not found for deletion: {}", filePath);
            }
          } catch (IOException e) {
            log.error("Error deleting media file: {}", mediaInfo.filename(), e);
            throw new CompletionException(
                new FileDeleteException("Error deleting media file: " + mediaInfo.filename(), e));
          }
        });
  }
}
