package com.app.module.user.application.event;

import com.app.module.user.domain.entity.User;
import com.app.module.user.infrastructure.user.UserDao;
import com.app.shared.event.UserMediaCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class UserEventListener {
  private final UserDao userDao;

  @ApplicationModuleListener
  public void on(UserMediaCreatedEvent event) {
    log.info(
        "Receive UserMediaCreatedEvent [userId={}, mediaId={}, createAt={}]",
        event.userId(),
        event.mediaId(),
        event.createAt());

    User user = userDao.findUserById(event.userId());
    if (user == null) {
      log.warn("User {} not found for media {}", event.userId(), event.mediaId());
      return;
    }

    user.setMediaId(event.mediaId());
  }
}
