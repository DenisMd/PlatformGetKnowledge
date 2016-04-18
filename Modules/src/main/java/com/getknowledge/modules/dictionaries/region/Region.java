package com.getknowledge.modules.dictionaries.region;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.dictionaries.city.City;
import com.getknowledge.modules.dictionaries.country.Country;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "region")
@ModuleInfo(repositoryName = "RegionRepository", serviceName = "RegionService")
public class Region extends AbstractEntity {

    @Column(name = "region_name" , nullable = false)
    private String regionName;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Country country;

    @OneToMany(mappedBy = "region")
    @JsonIgnore
    private List<City> cities;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
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
