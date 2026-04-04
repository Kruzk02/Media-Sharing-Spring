package com.app.module.user.application.service;

import com.app.module.user.domain.entity.Follower;
import com.app.module.user.domain.entity.User;
import java.util.List;

public interface FollowerService {
  List<User> getAllFollowingByUserId(long userId, int limit);

  Follower followUser(long followerId);

  void unfollowUser(long followerId);
}
