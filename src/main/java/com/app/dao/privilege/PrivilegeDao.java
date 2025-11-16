package com.app.dao.privilege;

import com.app.dao.base.Creatable;
import com.app.model.Privilege;

public interface PrivilegeDao extends Creatable<Privilege> {
  Privilege findByName(String name);
}
