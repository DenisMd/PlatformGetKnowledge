package com.getknowledge.modules.shop.programs.crm;

import com.getknowledge.modules.shop.item.Item;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "crm_discount_programs")
public class DiscountProgram extends AbstractEntity {

    private String name;

    @Column(length = 1000)
    private String note;

    @ManyToMany
    private List<Item> items;

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
