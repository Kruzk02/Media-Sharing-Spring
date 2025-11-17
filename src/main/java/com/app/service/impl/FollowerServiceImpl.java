package com.app.service.impl;

import com.app.dao.follower.FollowerDao;
import com.app.exception.sub.UserNotFoundException;
import com.app.model.Follower;
import com.app.service.FollowerService;
import com.app.user.dao.user.UserDao;
import com.app.user.model.User;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FollowerServiceImpl implements FollowerService {

  private final FollowerDao followerDao;
  private final UserDao userDao;

  private User getAuthenticationUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userDao.findUserByUsername(authentication.getName());
  }

  @Override
  public List<User> getAllFollowingByUserId(long userId, int limit) {
    List<User> users = followerDao.getAllFollowingByUserId(userId, limit);
    if (users.isEmpty()) {
      return Collections.emptyList();
    }
    return users;
  }

  @Override
  public Follower followUser(long userIdToFollow) {
    User userToFollow = userDao.findUserById(userIdToFollow);
    if (userToFollow == null) {
      throw new UserNotFoundException("User not found with a id: " + userIdToFollow);
    }

    long authUserId = getAuthenticationUser().getId();

    if (Objects.equals(authUserId, userToFollow.getId())) {
      throw new IllegalArgumentException("Users cannot follow themselves.");
    }

    if (followerDao.isFollowing(authUserId, userToFollow.getId())) {
      throw new RuntimeException("User is already following.");
    }

    return followerDao.followUser(authUserId, userToFollow.getId());
  }

  @Override
  public void unfollowUser(long followerId) {
    User userToUnfollow = userDao.findUserById(followerId);
    if (userToUnfollow == null) {
      throw new UserNotFoundException("User not found with ID: " + followerId);
    }

    long authUserId = getAuthenticationUser().getId();

    if (!followerDao.isFollowing(authUserId, userToUnfollow.getId())) {
      throw new RuntimeException("User is not following.");
    }

    followerDao.unfollowUser(authUserId, userToUnfollow.getId());
  }
}
