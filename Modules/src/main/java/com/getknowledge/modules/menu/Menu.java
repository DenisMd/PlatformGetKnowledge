package com.getknowledge.modules.menu;

import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.platform.annotations.*;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.role.Role;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu")
@ModuleInfo(repositoryName = "MenuRepository" , serviceName = "MenuService")
public class Menu extends AbstractEntity{

    @Column(name = "name" , unique = true)
    private String name;

    @ManyToOne
    @com.getknowledge.platform.annotations.Access(roles = "ROLE_ADMIN")
    private Role role;

    @OneToMany
    private List<MenuItem> items = new ArrayList<>();

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        authorizationList.allowReadEveryOne = true;
        return authorizationList;
    }
}
