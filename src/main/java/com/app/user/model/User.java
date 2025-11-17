package com.app.user.model;

import com.app.model.Media;
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
  private Media media;
  private String bio;
  private Gender gender;
  private Boolean enable;
}
