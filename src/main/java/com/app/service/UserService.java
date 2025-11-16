package com.app.service;

import com.app.dto.request.LoginUserRequest;
import com.app.dto.request.RegisterUserRequest;
import com.app.dto.request.UpdateUserRequest;
import com.app.model.User;

public interface UserService {
  User register(RegisterUserRequest request);

  User login(LoginUserRequest request);

  User findFullUserByUsername(String username);

  User update(UpdateUserRequest request);

  void verifyAccount(String token);

  void resendVerifyToken();
}
