package com.getknowledge.modules.dictionaries.region;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.dictionaries.country.Country;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import org.hibernate.search.annotations.Field;

import javax.persistence.*;

@Entity
@Table(name = "region")
@ModuleInfo(repositoryName = "RegionRepository", serviceName = "RegionService")
public class Region extends AbstractEntity {

    @Column(name = "region_name")
    private String regionName;

    @JsonIgnore
    @Column(name = "xml_id")
    @Field
    private Long xmlId;

    @ManyToOne
    private Country country;

    @OneToOne
    private Language language;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Long getXmlId() {
        return xmlId;
    }

    public void setXmlId(Long xmlId) {
        this.xmlId = xmlId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        authorizationList.allowReadEveryOne = true;
        return authorizationList;
    }
}
