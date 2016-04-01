package com.getknowledge.modules.shop.item;

import com.getknowledge.modules.shop.price.Price;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "items")
@ModuleInfo(repositoryName = "ItemRepository" , serviceName = "ItemService")
public class Item extends AbstractEntity {

    @OneToOne(optional = false)
    private Price price;

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
