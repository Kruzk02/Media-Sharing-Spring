package com.app.dao.verificationtoken;

import com.app.dao.base.Creatable;
import com.app.model.VerificationToken;

public interface VerificationTokenDao extends Creatable<VerificationToken> {

  VerificationToken findByToken(String token);

  int deleteByToken(String token);

  void deleteExpiredTokens();
}
