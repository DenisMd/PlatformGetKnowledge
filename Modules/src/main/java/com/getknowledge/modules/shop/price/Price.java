package com.getknowledge.modules.shop.price;

import com.getknowledge.modules.dictionaries.currency.Currency;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "prices")
public class Price extends AbstractEntity {

    @ManyToOne
    private Currency currency;

    @Column
    private BigDecimal price = BigDecimal.ZERO;

    @Column
    private int discount = 0;

    @Column(nullable = false)
    private boolean free = false;

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        if (discount < 0 || discount >= 100) {
            throw new IllegalArgumentException("Discount is not correct");
        }
        this.discount = discount;
    }

    public BigDecimal totalCost() {
        if (isFree()) return new BigDecimal(0.0);

        if (discount == 0) return price;

        BigDecimal sell = price.multiply(new BigDecimal(String.valueOf(discount/100.0)));
        return price.subtract(sell);
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
