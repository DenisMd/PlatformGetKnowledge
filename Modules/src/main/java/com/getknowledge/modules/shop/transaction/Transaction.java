package com.getknowledge.modules.shop.transaction;

import com.getknowledge.modules.shop.item.Item;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.Calendar;

@Entity(name = "transactions")
@Table
@ModuleInfo(repositoryName = "TransactionRepository" , serviceName = "TransactionService")
public class Transaction extends AbstractEntity {

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar startDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType = TransactionType.NotStarted;

    @OneToOne(optional = false)
    private Item item;

    @OneToOne(optional = false)
    private UserInfo originator;

    @Column(length = 500)
    private String message;

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public UserInfo getOriginator() {
        return originator;
    }

    public void setOriginator(UserInfo originator) {
        this.originator = originator;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
