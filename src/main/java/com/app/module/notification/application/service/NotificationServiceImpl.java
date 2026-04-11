package com.app.module.notification.application.service;

import com.app.module.notification.application.exception.NotificationMessageIsEmptyException;
import com.app.module.notification.application.exception.NotificationNotFoundException;
import com.app.module.notification.domain.Notification;
import com.app.module.notification.infrastructure.NotificationDao;
import com.app.module.user.application.exception.UserNotFoundException;
import com.app.shared.dto.response.UserDTO;
import com.app.shared.gateway.UserGateway;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@AllArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

  private final NotificationDao notificationDao;
  private final UserGateway userGateway;
  private final Map<Long, SseEmitter> emitters;

  private UserDTO getAuthenticationUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userGateway.getUserByUsername(authentication.getName());
  }

  @Override
  public Notification save(Notification notification) {
    UserDTO user = userGateway.getUserById(notification.getUserId());
    if (user == null) {
      throw new UserNotFoundException("User not found with a id: " + notification.getUserId());
    }

    if (notification.getMessage().isEmpty()) {
      throw new NotificationMessageIsEmptyException("Notification message is empty");
    }

    Notification savedNotification = notificationDao.save(notification);
    SseEmitter emitter = emitters.get(user.id());
    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event().name("notification").data(savedNotification));
      } catch (IOException e) {
        emitters.remove(user.id());
        emitter.completeWithError(e);
      }
    }

    return savedNotification;
  }

  @Override
  public List<Notification> findByUserId(int limit, int offset, Boolean fetchUnread) {
    UserDTO user = getAuthenticationUser();
    if (user == null) {
      throw new UserNotFoundException("User not found");
    }

    List<Notification> notifications =
        notificationDao.findByUserId(user.id(), limit, offset, fetchUnread);
    if (notifications.isEmpty()) {
      return Collections.emptyList();
    }
    return notifications;
  }

  @Override
  public SseEmitter createEmitter(Long userId) {
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    emitters.put(userId, emitter);

    emitter.onCompletion(() -> emitters.remove(userId));
    emitter.onTimeout(() -> emitters.remove(userId));

    return emitter;
  }

  @Override
  public void deleteById(Long id) {
    Notification notification = notificationDao.findById(id);
    if (notification == null) {
      throw new NotificationNotFoundException("Notification not found with a id: " + id);
    }

    notificationDao.deleteById(notification.getId());
  }

  @Override
  public void markAsRead(Long notificationId) {
    Notification notification = notificationDao.findById(notificationId);
    if (notification == null) {
      throw new NotificationNotFoundException(
          "Notification not found with a id: " + notificationId);
    }

    notificationDao.markAsRead(notification.getId());
  }

  @Override
  public void markAllAsRead() {
    UserDTO user = getAuthenticationUser();
    notificationDao.markAllAsRead(user.id());
  }

  @Override
  public void deleteByUserId() {
    UserDTO user = getAuthenticationUser();
    notificationDao.deleteByUserId(user.id());
  }
}
