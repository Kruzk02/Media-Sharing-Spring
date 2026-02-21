package com.app.shared.pagination;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

public final class KeysetCursorCodec {
  public static String encode(LocalDateTime dateTime, Long id) {
    long epochMillis = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

    var byteBuffer = ByteBuffer.allocate(Long.BYTES * 2);
    byteBuffer.putLong(epochMillis);
    byteBuffer.putLong(id);

    return Base64.getUrlEncoder().withoutPadding().encodeToString(byteBuffer.array());
  }

  public static DecodedCursor decode(String cursor) {
    byte[] bytes = Base64.getUrlDecoder().decode(cursor);

    var buffer = ByteBuffer.wrap(bytes);
    long epochMillis = buffer.getLong();
    long id = buffer.getLong();

    var dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC);
    return new DecodedCursor(dateTime, id);
  }
}
