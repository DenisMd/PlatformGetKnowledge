package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.annotations.Access;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.PermissionRepository;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.RoleRepository;
import com.getknowledge.platform.modules.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class ProtectedRepository <T extends AbstractEntity> extends AbstractRepository<T> {
    protected User currentUser = null;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private T checkEntity(T entity) {
        if (entity == null) {return null;}
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            mainFor : for (Access access : field.getAnnotationsByType(Access.class)) {
                if (currentUser != null) {
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


    @Override
    @Transactional
    public T read(Long id, Class<T> classEntity) {
        return checkEntity(super.read(id, classEntity));
    }

    @Override
    @Transactional
    public List<T> list(Class<T> classEntity) {
        return super.list(classEntity).stream().map(this::checkEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<T> listPartial(Class<T> classEntity, int first, int max) {
        return super.listPartial(classEntity, first, max).stream().map(this::checkEntity).collect(Collectors.toList());
    }

    @Transactional
    public List<T> getEntitiesByFieldAndValue(Class<T> classEntity ,String field, Object value) {
        return super.getEntitiesByFieldAndValue(classEntity , field,value).stream().map(this::checkEntity).collect(Collectors.toList());
    }
}
