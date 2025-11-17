package com.app.follower.service;

import com.app.follower.model.Follower;
import com.app.user.model.User;
import java.util.List;

public interface FollowerService {
  List<User> getAllFollowingByUserId(long userId, int limit);

  Follower followUser(long followerId);

  void unfollowUser(long followerId);
}
