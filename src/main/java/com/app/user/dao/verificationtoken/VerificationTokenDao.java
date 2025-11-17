package com.app.user.dao.verificationtoken;

import com.app.dao.base.Creatable;
import com.app.user.model.VerificationToken;

public interface VerificationTokenDao extends Creatable<VerificationToken> {

  VerificationToken findByToken(String token);

  int deleteByToken(String token);

  void deleteExpiredTokens();
}
