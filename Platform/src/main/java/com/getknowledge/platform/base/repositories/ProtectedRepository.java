package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.annotations.Access;
import com.getknowledge.platform.base.entities.CloneableEntity;
import com.getknowledge.platform.base.entities.IOwner;
import com.getknowledge.platform.base.entities.IUser;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.user.User;

import java.lang.reflect.Field;

public abstract class ProtectedRepository <T extends CloneableEntity<T>> extends BaseRepository<T> implements PrepareEntity<T>  {

    @Override
    public T prepare(T entity,User currentUser) {
        if (entity == null) {return null;}
        User owner = null;
        if (entity instanceof IUser) {
            owner = ((IUser)entity).getUser();
        }

        boolean isOwner = false;

        if (entity instanceof IOwner) {
            isOwner = ((IOwner)entity).isOwner(currentUser);
        }

        entity = entity.clone();
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            mainFor : for (Access access : field.getAnnotationsByType(Access.class)) {
                if (currentUser != null) {

                    if (access.myself()) {
                        if (owner != null) {
                            if (owner.getLogin().equals(currentUser.getLogin())) {
                                break;
                            }
                        }
                    }

                    if (access.forOwners()) {
                        if (isOwner) {
                            break;
                        }
                    }

                    for (String permission : access.permissions()) {
                        if (permission.isEmpty()) continue;
                        Permission p = new Permission();
                        p.setPermissionName(permission);
                        if (currentUser.isHasPermission(p)) {
                            break mainFor;
                        }
                    }

                    for (String role : access.roles()) {
                        if (role.isEmpty()) continue ;
                        Role r = new Role();
                        r.setRoleName(role);
                        if (currentUser.isHasRole(r)) {
                            break mainFor;
                        }
                    }
                }

                try {
                    field.setAccessible(true);
                    field.set(entity, null);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return entity;
    }
}
