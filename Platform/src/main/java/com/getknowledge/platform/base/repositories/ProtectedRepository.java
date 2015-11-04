package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.annotations.Access;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.IUser;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ProtectedRepository <T extends AbstractEntity> extends PrepareRepository<T> {
    protected User currentUser = null;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @Override
    public T prepare(T entity) {
        if (entity == null) {return null;}
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            mainFor : for (Access access : field.getAnnotationsByType(Access.class)) {
                if (currentUser != null) {

                    if (access.myself()) {
                        if (entity instanceof IUser) {
                            User user = ((IUser)entity).getUser();
                            if (user != null) {
                                if (user.getLogin().equals(currentUser.getLogin())) {
                                    break mainFor;
                                }
                            }
                        }
                    }

                    for (String permission : access.permissions()) {
                        Permission p = new Permission();
                        p.setPermissionName(permission);
                        if (currentUser.isHasPermission(p)) {
                            break mainFor;
                        }
                    }

                    for (String role : access.roles()) {
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
