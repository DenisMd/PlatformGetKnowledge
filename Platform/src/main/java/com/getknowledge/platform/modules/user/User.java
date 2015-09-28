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
import java.util.List;

@Entity
@ModuleInfo(repositoryName = "UserRepository" , serviceName = "UserService")
@Table(name = "sys_user")
public class User extends AbstractEntity {

    @Column(unique = true)
    private String login;

    @Column(name = "hash_pwd" , length = 500)
    @JsonIgnore
    private String hashPwd;

    @Transient
    private String pwdTransient;

    private boolean enabled=true;

    @ManyToOne
    private Role role;

    @OneToMany
    private List<Permission> permissions = new ArrayList<>();

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getHashPwd() {
        return hashPwd;
    }

    public void setHashPwd(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.hashPwd = bCryptPasswordEncoder.encode(password);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
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
}
