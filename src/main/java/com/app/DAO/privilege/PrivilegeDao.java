package com.app.DAO.privilege;

import com.app.DAO.base.Creatable;
import com.app.Model.Privilege;

public interface PrivilegeDao extends Creatable<Privilege> {
  Privilege findByName(String name);
}
