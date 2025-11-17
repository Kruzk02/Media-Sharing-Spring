package com.app.module.user.service;

import com.app.module.user.dto.request.LoginUserRequest;
import com.app.module.user.dto.request.RegisterUserRequest;
import com.app.module.user.dto.request.UpdateUserRequest;
import com.app.module.user.model.User;

public interface UserService {
  User register(RegisterUserRequest request);

  User login(LoginUserRequest request);

  User findFullUserByUsername(String username);

  User update(UpdateUserRequest request);

  void verifyAccount(String token);

  void resendVerifyToken();
}
