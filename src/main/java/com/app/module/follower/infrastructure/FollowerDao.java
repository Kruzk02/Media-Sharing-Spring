package com.app.module.follower.infrastructure;

import com.app.module.follower.domain.Follower;
import com.app.module.user.domain.entity.User;
import java.util.List;

public interface FollowerDao {
  List<User> getAllFollowingByUserId(long userId, int limit);

  boolean isFollowing(long authUserId, long targetId);

  Follower followUser(long authUserId, long targetId);

  int unfollowUser(long authUserId, long targetId);
}
