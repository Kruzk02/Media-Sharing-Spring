package com.app.module.user.dao.verificationtoken;

import com.app.module.user.domain.entity.VerificationToken;
import com.app.shared.dao.Creatable;

public interface VerificationTokenDao extends Creatable<VerificationToken> {

  VerificationToken findByToken(String token);

  int deleteByToken(String token);

  void deleteExpiredTokens();
}
