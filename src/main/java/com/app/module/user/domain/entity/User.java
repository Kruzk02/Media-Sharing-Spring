package com.app.module.user.domain.entity;

import com.app.module.user.domain.status.Gender;
import java.io.Serializable;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User implements Serializable {

  private Long id;
  private String username;
  private String email;
  private String password;
  private List<Role> roles;
  private Long mediaId;
  private String bio;
  private Gender gender;
  private Boolean enable;
}
