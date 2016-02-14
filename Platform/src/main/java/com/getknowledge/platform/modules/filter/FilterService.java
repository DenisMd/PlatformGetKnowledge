package com.getknowledge.platform.modules.filter;

import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.repositories.FilterQuery;
import com.getknowledge.platform.base.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service(value = "FilterService")
public class FilterService extends AbstractService {

    @Transactional
    public List<AbstractEntity> getList(FilterQuery filterQuery , int first , int max) {
        List<AbstractEntity> abstractEntities = filterQuery.getQuery(first,max).getResultList();
        return abstractEntities;
    }

    @Transactional
    public Long getCount(FilterQuery filterQuery){
        return filterQuery.count();
    }

}
