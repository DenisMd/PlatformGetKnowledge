package com.getknowledge.modules.dictionaries.region;

import com.getknowledge.modules.dictionaries.country.Country;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("RegionRepository")
public class RegionRepository extends BaseRepository<Region> {
    @Override
    protected Class<Region> getClassEntity() {
        return Region.class;
    }

    public Region create(String name, Country country) {
        Region region = new Region();
        region.setCountry(country);
        region.setRegionName(name);
        create(region);
        return region;
    }
}
