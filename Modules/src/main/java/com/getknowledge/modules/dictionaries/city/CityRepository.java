package com.getknowledge.modules.dictionaries.city;

import com.getknowledge.modules.dictionaries.region.Region;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("CityRepository")
public class CityRepository extends BaseRepository<City> {
    @Override
    protected Class<City> getClassEntity() {
        return City.class;
    }

    public void create(String name, Region region){
        City city = new City();
        city.setCityName(name);
        city.setRegion(region);
        create(city);
    }
}
