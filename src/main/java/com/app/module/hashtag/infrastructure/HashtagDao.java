package com.app.module.hashtag.infrastructure;

import com.app.module.hashtag.domain.Hashtag;
import com.app.shared.dao.Creatable;
import java.util.Map;
import java.util.Set;

public interface HashtagDao extends Creatable<Hashtag> {
  Map<String, Hashtag> findByTag(Set<String> tags);
}
