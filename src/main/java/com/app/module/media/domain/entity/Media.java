package com.app.module.media.domain.entity;

import com.app.module.media.domain.status.MediaType;
import com.app.shared.type.Status;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Media implements Serializable {

  private Long id;
  private String url;
  private Status status;
  private MediaType mediaType;
  private Timestamp created_at;
}
