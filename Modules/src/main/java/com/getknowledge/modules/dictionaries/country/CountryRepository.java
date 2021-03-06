package com.getknowledge.modules.dictionaries.country;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("CountryRepository")
public class CountryRepository extends BaseRepository<Country> {
    @Override
    protected Class<Country> getClassEntity() {
        return Country.class;
    }


    public Country createCountry(String name){
        Country country = new Country();
        country.setCountryName(name);
        create(country);
        return country;
    }
}
