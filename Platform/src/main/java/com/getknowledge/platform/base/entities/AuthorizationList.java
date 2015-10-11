package com.getknowledge.platform.base.entities;


import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.user.User;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationList {

    private List<User> userList = new ArrayList<>();
    private List<Permission> permissionsForRead = new ArrayList<>();
    private List<Permission> permissionsForEdit = new ArrayList<>();
    public boolean allowCreateEveryOne = false;

    public List<User> getUserList() {
        return userList;
    }

    public List<Permission> getPermissionsForEdit() {
        return permissionsForEdit;
    }

    public List<Permission> getPermissionsForRead() {
        return permissionsForRead;
    }
}
