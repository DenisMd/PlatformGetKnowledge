package com.getknowledge.platform.base.services;

import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.modules.user.User;

import java.util.HashMap;
import java.util.Objects;

public abstract class AuthorizedService<T extends AbstractEntity> extends AbstractService {

    private HashMap<String,Object> getMapFromUser(User currentUser) {
        HashMap<String,Object> data = new HashMap<>();
        data.put("principalName",currentUser == null ? "" : currentUser.getLogin());
        return data;
    }

    public boolean isAccessForRead(User currentUser, T entity) {
        return isAccessToRead(getMapFromUser(currentUser),entity);
    }

    public boolean isAccessForCreate(User currentUser, T entity) {
        return isAccessToCreate(getMapFromUser(currentUser),entity);
    }

    public boolean isAccessForRemove(User currentUser, T entity) {
        return isAccessToRemove(getMapFromUser(currentUser),entity);
    }

    public boolean isAccessForEdit(User currentUser, T entity) {
        return isAccessToEdit(getMapFromUser(currentUser),entity);
    }

}
