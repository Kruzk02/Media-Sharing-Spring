package com.app.dao.notification;

import com.app.dao.base.Creatable;
import com.app.dao.base.Deletable;
import com.app.dao.base.Readable;
import com.app.model.Notification;
import java.util.List;

public interface NotificationDao
    extends Creatable<Notification>, Readable<Notification>, Deletable {
  List<Notification> findByUserId(Long userId, int limit, int offset, Boolean fetchUnread);

  void markAsRead(Long notificationId);

  void markAllAsRead(Long userId);

  void deleteByUserId(Long userId);
}
