package com.app.module.notification.application.service;

import com.app.module.notification.domain.Notification;
import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
  Notification save(Notification notification);

  List<Notification> findByUserId(int limit, int offset, Boolean fetchUnread);

  SseEmitter createEmitter(Long userId);

  void deleteById(Long id);

  void markAsRead(Long notificationId);

  void markAllAsRead();

  void deleteByUserId();
}
