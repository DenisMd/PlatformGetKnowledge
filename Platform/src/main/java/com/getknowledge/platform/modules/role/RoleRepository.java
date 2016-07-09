package com.getknowledge.platform.modules.role;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.modules.role.names.BaseRoleName;
import org.springframework.stereotype.Repository;

@Repository(value = "RoleRepository")
public class RoleRepository extends BaseRepository<Role> {

    @Override
    protected Class<Role> getClassEntity() {
        return Role.class;
    }

    public Role getRoleByName(String roleName){
        return getSingleEntityByFieldAndValue("roleName",roleName);
    }
}
