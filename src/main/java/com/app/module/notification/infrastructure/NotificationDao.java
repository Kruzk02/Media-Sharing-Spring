package com.app.module.notification.infrastructure;

import com.app.module.notification.domain.Notification;
import com.app.shared.dao.Creatable;
import com.app.shared.dao.Deletable;
import com.app.shared.dao.Readable;
import java.util.List;

public interface NotificationDao
    extends Creatable<Notification>, Readable<Notification>, Deletable {
  List<Notification> findByUserId(Long userId, int limit, int offset, Boolean fetchUnread);

  void markAsRead(Long notificationId);

  void markAllAsRead(Long userId);

  void deleteByUserId(Long userId);
}
