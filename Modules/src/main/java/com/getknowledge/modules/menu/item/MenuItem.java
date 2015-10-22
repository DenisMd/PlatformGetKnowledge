package com.getknowledge.modules.menu.item;

import com.getknowledge.modules.menu.Menu;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu_items")
@ModuleInfo(repositoryName = "MenuItemsRepository" , serviceName = "MenuItemsService")
public class MenuItem extends AbstractEntity {

    private String title;

    private String url;

    @OneToMany
    @JoinTable(name = "subItems_of_item")
    private List<MenuItem> subItems = new ArrayList<>();

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @Column(name = "color" , length = 20)
    private String color;

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
        return authorizationList;
    }
}