package com.app.module.user.infrastructure.privilege;

import com.app.module.user.domain.entity.Privilege;
import com.app.shared.dao.Creatable;

public interface PrivilegeDao extends Creatable<Privilege> {
  Privilege findByName(String name);
}
