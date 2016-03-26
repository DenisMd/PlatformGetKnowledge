package com.getknowledge.modules.dictionaries.city;

import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.modules.dictionaries.region.Region;
import com.getknowledge.modules.dictionaries.region.RegionRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service("CityService")
public class CityService extends AbstractService {
    @Autowired
    private RegionRepository regionRepository;

    @Action(name = "getCitiesByRegion" , mandatoryFields = {"regionId"})
    @Transactional
    public List<City> getCities(HashMap<String,Object> data) {
        Long regionId = longFromField("regionId" ,data);
        Region region = regionRepository.read(regionId);
        return region == null ? null : region.getCities();
    }
}
