package com.app.module.user.dao.privilege;

import com.app.module.user.model.Privilege;
import com.app.shared.dao.Creatable;

public interface PrivilegeDao extends Creatable<Privilege> {
  Privilege findByName(String name);
}
