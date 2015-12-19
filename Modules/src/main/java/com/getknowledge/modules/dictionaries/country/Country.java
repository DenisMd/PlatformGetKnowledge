package com.getknowledge.modules.dictionaries.country;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import org.hibernate.search.annotations.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "country")
@ModuleInfo(repositoryName = "CountryRepository" , serviceName = "CountryService")
public class Country extends AbstractEntity {


    @Column(name = "country_name")
    private String countryName;

    @JsonIgnore
    @Column(name = "external_id")
    private Long xmlId;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Long getXmlId() {
        return xmlId;
    }

    public void setXmlId(Long xmlId) {
        this.xmlId = xmlId;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowReadEveryOne = true;
        return authorizationList;
    }
}
