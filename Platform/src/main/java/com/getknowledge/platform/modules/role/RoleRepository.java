package com.getknowledge.platform.modules.role;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.DeleteException;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.role.names.RoleName;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

@Repository(value = "RoleRepository")
public class RoleRepository extends BaseRepository<Role> {

    @Override
    protected Class<Role> getClassEntity() {
        return Role.class;
    }

    @Override
    public void remove(Long id) throws PlatformException {
        Role role = read(id);

        if (role == null) return;

        if (!role.getUsers().isEmpty()) {
            throw new DeleteException("Error remove role : " + role.getRoleName() + " constrain by user");
        }

        super.remove(id);
    }

    public Role getRoleByName(String roleName){
        return getSingleEntityByFieldAndValue("roleName",roleName);
    }

    public Role getRole(RoleName roleName){
        Role role = getSingleEntityByFieldAndValue("roleName" , roleName.name());
        return role;
    }
}
