package com.getknowledge.modules.section;

import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import org.hibernate.search.annotations.Field;

import javax.persistence.*;

@Entity
@Table(name = "section" , indexes =
        {@Index(name="index_by_name" , columnList = "name,language",unique = true)})
@ModuleInfo(repositoryName = "SectionRepository" , serviceName = "SectionService")
public class Section extends AbstractEntity {

    @Column
    private String name;

    @Column(columnDefinition = "Text" , name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "menu_item")
    private MenuItem menuItem;

    @Basic(fetch=FetchType.LAZY)
    @Lob @Column(name="cover")
    private byte[] cover;

    @ManyToOne
    @JoinColumn(name = "language" , nullable = false)
    private Language language;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowReadEveryOne = true;
        return authorizationList;
    }
}
