package com.getknowledge.modules.shop.programs.crm;

import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "crm_discounts_program")
public class DiscountProgram extends AbstractEntity {

    private String name;

    @Column(length = 1000)
    private String note;

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
