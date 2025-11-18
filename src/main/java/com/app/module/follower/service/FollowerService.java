package com.app.module.follower.service;

import com.app.module.follower.model.Follower;
import com.app.module.user.domain.entity.User;
import java.util.List;

public interface FollowerService {
  List<User> getAllFollowingByUserId(long userId, int limit);

  Follower followUser(long followerId);

  void unfollowUser(long followerId);
}
