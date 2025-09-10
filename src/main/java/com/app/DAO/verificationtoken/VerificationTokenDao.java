package com.app.DAO.verificationtoken;

import com.app.DAO.base.Creatable;
import com.app.Model.VerificationToken;

public interface VerificationTokenDao extends Creatable<VerificationToken> {

  VerificationToken findByToken(String token);

  int deleteByToken(String token);

  void deleteExpiredTokens();
}
