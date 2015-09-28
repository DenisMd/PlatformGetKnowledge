package com.getknowledge.platform.base.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_name",length = 500)
    private String className;

    public Long getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @JsonIgnore
    public abstract AuthorizationList getAuthorizationList();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEntity)) return false;

        AbstractEntity that = (AbstractEntity) o;

        if (!className.equals(that.className)) return false;
        if (!id.equals(that.id)) return false;

        return true;
    }
}
