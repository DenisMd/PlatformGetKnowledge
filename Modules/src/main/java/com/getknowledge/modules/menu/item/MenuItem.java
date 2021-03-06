package com.getknowledge.modules.menu.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.menu.Menu;
import com.getknowledge.modules.platform.auth.PermissionNames;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu_items")
@ModuleInfo(repositoryName = "MenuItemsRepository")
public class MenuItem extends AbstractEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    @OneToMany(mappedBy = "parent")
    @OrderBy("id")
    private List<MenuItem> subItems = new ArrayList<>();

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @Column(name = "color" , length = 20)
    private String color;

    @ManyToOne
    @JsonIgnore
    private Menu menu;

    @ManyToOne
    @JsonIgnore
    private MenuItem parent;

    public MenuItem getParent() {
        return parent;
    }

    public void setParent(MenuItem parent) {
        this.parent = parent;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public List<MenuItem> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<MenuItem> subItems) {
        this.subItems = subItems;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditMenu()));
        return authorizationList;
    }
}
