package com.getknowledge.platform.base.entities;


import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.role.names.BaseRoleName;
import com.getknowledge.platform.modules.user.User;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationList {

    private List<User> userList = new ArrayList<>();
    private List<Permission> permissionsForRead = new ArrayList<>();
    private List<Permission> permissionsForEdit = new ArrayList<>();
    private List<Permission> permissionsForCreate = new ArrayList<>();
    private List<Permission> permissionsForRemove = new ArrayList<>();
    public boolean readFromAuthorizedService = false;
    public boolean editFromAuthorizedService = false;
    public boolean createFromAuthorizedService = false;
    public boolean removeFromAuthorizedService = false;
    public boolean allowCreateEveryOne = false;
    public boolean allowReadEveryOne = false;
    public boolean allowUseAuthorizedService = false;

    public List<User> getUserList() {
        return userList;
    }

    public List<Permission> getPermissionsForEdit() {
        return permissionsForEdit;
    }

    public List<Permission> getPermissionsForRead() {
        return permissionsForRead;
    }

    public List<Permission> getPermissionsForCreate() {
        return permissionsForCreate;
    }

    public List<Permission> getPermissionsForRemove() {
        return permissionsForRemove;
    }

    private boolean checkRights(User user , List<Permission> permissions) {

        if (user == null || permissions == null || permissions.isEmpty()) return false;

        for (Permission element : user.getPermissions()) {
            if (permissions.contains(element)) return true;
        }

        for (Permission element : user.getRole().getPermissions()) {
            if (permissions.contains(element)) return true;
        }

        return false;
    }

    private boolean checkUserList(User user, List<User> users) {
        return users != null && users.contains(user);
    }

    public boolean isAccessRead (User currentUser) {
        if (currentUser == null) return false;

        if (currentUser.getRole().getRoleName().equals(BaseRoleName.ROLE_ADMIN())) {
            return true;
        }

        return checkRights(currentUser, getPermissionsForRead()) || checkUserList(currentUser, getUserList());
    }

    public boolean isAccessCreate (User currentUser) {
        if (currentUser == null) return false;

        if (currentUser.getRole().getRoleName().equals(BaseRoleName.ROLE_ADMIN())) {
            return true;
        }

        return checkRights(currentUser, getPermissionsForCreate()) || checkUserList(currentUser, getUserList());
    }

    public boolean isAccessEdit (User currentUser) {
        if (currentUser == null) return false;

        if (currentUser.getRole().getRoleName().equals(BaseRoleName.ROLE_ADMIN())) {
            return true;
        }

        return checkRights(currentUser, getPermissionsForEdit()) || checkUserList(currentUser, getUserList());
    }

    public boolean isAccessRemove (User currentUser) {
        if (currentUser == null) return false;

        if (currentUser.getRole().getRoleName().equals(BaseRoleName.ROLE_ADMIN())) {
            return true;
        }

        return checkRights(currentUser, getPermissionsForRemove()) || checkUserList(currentUser, getUserList());
    }


}
