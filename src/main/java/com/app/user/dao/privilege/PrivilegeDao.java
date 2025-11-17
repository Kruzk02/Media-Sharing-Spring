package com.app.user.dao.privilege;

import com.app.dao.base.Creatable;
import com.app.user.model.Privilege;

public interface PrivilegeDao extends Creatable<Privilege> {
  Privilege findByName(String name);
}
