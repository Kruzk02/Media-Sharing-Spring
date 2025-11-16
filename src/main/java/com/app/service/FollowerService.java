package com.app.service;

import com.app.model.Follower;
import com.app.model.User;
import java.util.List;

public interface FollowerService {
  List<User> getAllFollowingByUserId(long userId, int limit);

  Follower followUser(long followerId);

  void unfollowUser(long followerId);
}
