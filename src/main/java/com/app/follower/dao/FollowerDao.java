package com.app.follower.dao;

import com.app.follower.model.Follower;
import com.app.user.model.User;
import java.util.List;

public interface FollowerDao {
  List<User> getAllFollowingByUserId(long userId, int limit);

  boolean isFollowing(long authUserId, long targetId);

  Follower followUser(long authUserId, long targetId);

  int unfollowUser(long authUserId, long targetId);
}
