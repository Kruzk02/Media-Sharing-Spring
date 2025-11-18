package com.app.module.user.dao.role;

import com.app.module.user.domain.entity.Role;
import com.app.shared.dao.Creatable;

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
