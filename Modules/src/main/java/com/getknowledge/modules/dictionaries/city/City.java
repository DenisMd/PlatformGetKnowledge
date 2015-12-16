package com.getknowledge.modules.dictionaries.city;

import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.dictionaries.region.Region;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;

@Entity
@Table(name = "city")
@ModuleInfo(repositoryName = "CityRepository" , serviceName = "CityService")
public class City extends AbstractEntity {

    @Column(name = "city_name")
    private String cityName;

    @ManyToOne
    private Region region;

    @OneToOne
    private Language language;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        authorizationList.allowReadEveryOne = true;
        return authorizationList;
    }
}
