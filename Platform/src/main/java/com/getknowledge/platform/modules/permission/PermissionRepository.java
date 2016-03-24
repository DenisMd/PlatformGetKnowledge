package com.getknowledge.platform.modules.permission;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.permission.names.PermissionNames;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.RoleRepository;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("PermissionRepository")
public class PermissionRepository extends BaseRepository<Permission> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    protected Class<Permission> getClassEntity() {
        return Permission.class;
    }

    public Permission ifNotExistCreate(Permission permission) {
        Permission finedPermission = getSingleEntityByFieldAndValue("permissionName", permission.getPermissionName());
        if (finedPermission == null) create(permission);
        return permission;
    }

    public Permission getPermissionByName (PermissionNames permissionNames) {
        Permission permission = getSingleEntityByFieldAndValue("permissionName" , permissionNames.getName());
        return permission;
    }

    @Override
    @Transactional
    public void remove(Long id) throws PlatformException {
        Permission permission = read(id);
        if (permission != null) {
            List<User> users = entityManager.createQuery("select u from User u join u.permissions as p where p.id = :id").
                setParameter("id" , permission.getId()).getResultList();
            if (!users.isEmpty()) {
                users.forEach(u -> {
                    u.getPermissions().remove(permission);
                    userRepository.update(u);
                });
            }
            List<Role> roles = entityManager.createQuery("select r from Role r join r.permissions as p where p.id = :id")
                    .setParameter("id" , permission.getId()).getResultList();
            if (!roles.isEmpty()) {
                roles.forEach(r -> {
                    r.getPermissions().remove(permission);
                    roleRepository.update(r);
                });
            }
            super.remove(id);
        }
    }
}
