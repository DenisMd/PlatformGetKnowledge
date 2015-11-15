package com.getknowledge.modules.dictionaries.region;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("RegionRepository")
public class RegionRepository extends BaseRepository<Region> {
    @Override
    protected Class<Region> getClassEntity() {
        return Region.class;
    }
}
