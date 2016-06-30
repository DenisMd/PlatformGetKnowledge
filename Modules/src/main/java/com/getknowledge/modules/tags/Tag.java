package com.getknowledge.modules.tags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.books.Book;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tags")
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn( name = "type" )
public abstract class Tag extends AbstractEntity {

    @Column(nullable = false)
    private String tagName;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName.toLowerCase();
    }

    @JsonIgnore
    public abstract List<EntityWithTags> getEntities();

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
