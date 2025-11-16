package com.app.dao.hashtag;

import com.app.dao.base.Creatable;
import com.app.model.Hashtag;
import java.util.Map;
import java.util.Set;

public interface HashtagDao extends Creatable<Hashtag> {
  Map<String, Hashtag> findByTag(Set<String> tags);
}
