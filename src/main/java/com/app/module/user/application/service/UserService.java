package com.app.module.user.application;

import com.app.module.user.domain.entity.User;
import com.app.module.user.application.dto.request.LoginUserRequest;
import com.app.module.user.application.dto.request.RegisterUserRequest;
import com.app.module.user.application.dto.request.UpdateUserRequest;

public interface UserService {
  User register(RegisterUserRequest request);

  User login(LoginUserRequest request);

  User findFullUserByUsername(String username);

  User update(UpdateUserRequest request);

  void verifyAccount(String token);

  void resendVerifyToken();
}
