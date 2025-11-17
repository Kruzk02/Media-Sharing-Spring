package com.app.user.dao.role;

import com.app.dao.base.Creatable;
import com.app.user.model.Role;

/** Interface for managing Role data access operations. */
public interface RoleDao extends Creatable<Role> {

  /**
   * Finds a role by role name.
   *
   * @param name the name of the role to find.
   * @return the role found, or null if not found.
   */
  Role findByName(String name);
}
