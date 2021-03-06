package com.getknowledge.platform.modules.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.role.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@ModuleInfo(repositoryName = "UserRepository" , serviceName = "UserService")
@Table(name = "sys_user")
public class User extends AbstractEntity {

    @Column(unique = true)
    private String login;

    @Column(name = "hash_pwd" , length = 500 , nullable = false)
    @JsonIgnore
    private String hashPwd;

    @Transient
    private String pwdTransient;

    @Column(nullable = false)
    private boolean enabled=true;

    @Column(nullable = false)
    private boolean blocked=false;

    @ManyToOne(optional = false)
    private Role role;

    @Column(name = "block_message",length = 500)
    private String blockMessage;

    @ManyToMany
    @JoinTable(name = "sys_permissions_of_user")
    private List<Permission> permissions = new ArrayList<>();

    @Column(name = "create_date" , nullable = false)
    private Calendar createDate;

    public String getBlockMessage() {
        return blockMessage;
    }

    public void setBlockMessage(String blockMessage) {
        this.blockMessage = blockMessage;
    }

    public User() {
        createDate = Calendar.getInstance();
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getHashPwd() {
        return hashPwd;
    }

    public void hashRawPassword (String rawPassword) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.hashPwd = bCryptPasswordEncoder.encode(rawPassword);
    }

    public void setHashPwd(String hashPwd) {
        this.hashPwd = hashPwd;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        authorizationList.allowReadEveryOne = false;
        return authorizationList;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPwdTransient() {
        return pwdTransient;
    }

    public void setPwdTransient(String pwdTransient) {
        this.pwdTransient = pwdTransient;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public boolean isHasPermission (Permission permission) {

        boolean result = false;
        result = permissions != null && permissions.contains(permission);
        if (result) return result;

        if (role != null) {
            result = role.getPermissions() != null && role.getPermissions().contains(permission);
        }

        return result;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean isHasRole (Role role) {
        return this.role.equals(role);
    }
}
