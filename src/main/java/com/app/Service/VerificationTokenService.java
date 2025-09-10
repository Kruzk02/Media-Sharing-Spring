package com.app.Service;

import com.app.DAO.user.UserDao;
import com.app.DAO.VerificationTokenDao;
import com.app.Model.User;
import com.app.Model.VerificationToken;
import com.app.exception.sub.TokenExpireException;
import com.app.exception.sub.UserNotMatchException;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VerificationTokenService {

  private final VerificationTokenDao verificationTokenDao;
  private final UserDao userDao;

  public VerificationToken generateVerificationToken(User user) {
    String token = String.valueOf((int) (Math.random() * 900000) + 100000);
    VerificationToken verificationToken =
        VerificationToken.builder()
            .token(token)
            .userId(user.getId())
            .expireDate(LocalDateTime.now().plusMinutes(10))
            .build();
    return verificationTokenDao.create(verificationToken);
  }

  public void verifyAccount(String token) {
    VerificationToken verificationToken = verificationTokenDao.findByToken(token);
    if (verificationToken == null) {
      throw new TokenExpireException("Verification token not found");
    }

    if (verificationToken.getExpireDate().isBefore(LocalDateTime.now())) {
      throw new TokenExpireException("Verification token expired");
    }

    if (userDao.checkAccountVerifyById(verificationToken.getUserId())) {
      throw new TokenExpireException("User already verified");
    }

    if (!verificationToken.getToken().equals(token)) {
      throw new TokenExpireException("Token not match");
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user1 = userDao.findUserByUsername(authentication.getName());
    User user = userDao.findUserById(verificationToken.getUserId());

    if (!Objects.equals(user.getId(), user1.getId())) {
      throw new UserNotMatchException("Wrong user");
    }

    user.setEnable(true);
    userDao.update(user);
    verificationTokenDao.deleteByToken(token);
  }

  @Scheduled(fixedRate = 10 * 60 * 1000)
  private void deleteExpireToken() {
    verificationTokenDao.deleteExpiredTokens();
  }
}
