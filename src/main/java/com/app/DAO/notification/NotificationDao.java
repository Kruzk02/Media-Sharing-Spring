package com.app.DAO.notification;

import com.app.DAO.base.Creatable;
import com.app.DAO.base.Deletable;
import com.app.Model.Notification;
import java.util.List;

public interface NotificationDao extends Creatable<Notification>, Deletable {
  List<Notification> findByUserId(Long userId, int limit, int offset, Boolean fetchUnread);

  Notification findById(Long id);

  void markAsRead(Long notificationId);

  void markAllAsRead(Long userId);

  void deleteByUserId(Long userId);
}
