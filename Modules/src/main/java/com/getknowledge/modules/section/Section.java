package com.getknowledge.modules.section;

import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;

@Entity
@Table(name = "section")
@ModuleInfo(repositoryName = "SectionRepository" , serviceName = "SectionService")
public class Section extends AbstractEntity {

    @Column
    private String title;

    @Column
    private String description;

    @OneToOne
    @JoinColumn(name = "menu_item")
    private MenuItem menuItem;

    @Basic(fetch=FetchType.LAZY)
    @Lob @Column(name="cover")
    private byte[] cover;

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
