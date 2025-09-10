package com.app.DAO.role;

import com.app.DAO.base.Creatable;
import com.app.Model.Role;

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
