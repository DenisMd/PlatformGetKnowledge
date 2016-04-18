package com.getknowledge.modules.shop.programs.crm;

import com.getknowledge.modules.shop.item.Item;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "crm_discount_programs")
public class DiscountProgram extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String note;

    @ManyToMany
    private List<Item> items;

    private int discount;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar endDate;

    @Column(name = "immediately_start" , nullable = false)
    private boolean immediatelyStart = false;

    public boolean isImmediatelyStart() {
        return immediatelyStart;
    }

    public void setImmediatelyStart(boolean immediatelyStart) {
        this.immediatelyStart = immediatelyStart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
