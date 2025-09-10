package com.app.DAO.hashtag;

import com.app.DAO.base.Creatable;
import com.app.Model.Hashtag;
import java.util.Map;
import java.util.Set;

public interface HashtagDao extends Creatable<Hashtag> {
  Map<String, Hashtag> findByTag(Set<String> tags);
}
