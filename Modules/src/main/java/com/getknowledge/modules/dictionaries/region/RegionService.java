package com.getknowledge.modules.dictionaries.region;

import com.getknowledge.modules.dictionaries.country.CountryRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("RegionService")
public class RegionService extends AbstractService {


    @Autowired
    private CountryRepository countryRepository;

    @Action(name = "chooseRegionByCountry" ,mandatoryFields = "country")
    public Region chooseRegionByCountry(HashMap<String,Object> data) {
        String countryName = (String) data.get("country");
        return null;
    }
}
