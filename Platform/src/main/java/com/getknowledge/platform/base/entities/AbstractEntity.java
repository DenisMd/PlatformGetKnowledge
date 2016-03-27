package com.getknowledge.platform.base.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public abstract AuthorizationList getAuthorizationList();

    @JsonIgnore
    public boolean isContinueIfNotEnoughRights() {
        //Данное значение учитывается при формировании списка
        //Сущность может решать пропустить ли ее при формировании списка
        //или кинуть исключение access denied
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEntity)) return false;

        AbstractEntity that = (AbstractEntity) o;

        return id.equals(that.id);
    }
}
