package com.getknowledge.modules.dictionaries.currency;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "currency")
@ModuleInfo(repositoryName = "CurrencyRepository" , serviceName = "CurrencyService")
public class Currency extends AbstractEntity {

    @Column
    private String name;

    @Column(length = 3)
    private String charCode;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "base_currency")
    private Boolean baseCurrency = false;

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public Boolean isBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Boolean baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public AuthorizationList getAuthorizationList() {

        //Обновлять может только администратор
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowReadEveryOne = true;
        authorizationList.allowCreateEveryOne = false;

        return authorizationList;
    }
}
