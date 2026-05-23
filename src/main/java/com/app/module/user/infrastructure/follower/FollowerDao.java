package com.app.module.user.infrastructure.follower;

import com.app.module.user.domain.entity.Follower;
import com.app.module.user.domain.entity.User;
import java.util.List;

public interface FollowerDao {
  List<User> getAllFollowingByUserId(long userId, int limit);

  boolean isFollowing(long authUserId, long targetId);

  Follower followUser(long authUserId, long targetId);

  int unfollowUser(long authUserId, long targetId);
}
