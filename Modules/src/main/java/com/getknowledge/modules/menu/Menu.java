package com.getknowledge.modules.menu;

import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu")
@ModuleInfo(repositoryName = "MenuRepository" , serviceName = "MenuService")
public class Menu extends AbstractEntity{

    @Column(name = "title")
    private String title;

    @OneToMany
    @JoinTable(name = "menu_items_join")
    private List<MenuItem> items = new ArrayList<>();

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();

        return authorizationList;
    }
}
