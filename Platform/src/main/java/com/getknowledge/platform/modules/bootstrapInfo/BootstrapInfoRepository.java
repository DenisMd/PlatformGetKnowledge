package com.getknowledge.platform.modules.bootstrapInfo;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.modules.bootstrapInfo.states.BootstrapState;
import com.getknowledge.platform.utils.ModuleLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository("BootstrapInfoRepository")
public class BootstrapInfoRepository extends BaseRepository<BootstrapInfo> {

    @Override
    protected Class<BootstrapInfo> getClassEntity() {
        return BootstrapInfo.class;
    }

    @Autowired
    private ModuleLocator moduleLocator;

    @Transactional
    public BootstrapInfo createIfNotExist(BootstrapInfo object) {
        if (object.getName() == null) return null;
        BootstrapInfo bootstrapInfo = getSingleEntityByFieldAndValue("name" , object.getName());
        if (bootstrapInfo != null) return bootstrapInfo;
        object.setBootstrapState(BootstrapState.NotComplete);
        super.create(object);
        return object;
    }

    @Override
    public List<BootstrapInfo> list() {
        List<BootstrapInfo> bootstrapInfos = new ArrayList<>(50);
        bootstrapInfos.addAll(moduleLocator.findAllBootstrapServices().stream().map
                (bootstrapInfo -> createIfNotExist(bootstrapInfo.getBootstrapInfo()))
                .collect(Collectors.toList()));

        bootstrapInfos.sort(new Comparator<BootstrapInfo>() {
            @Override
            public int compare(BootstrapInfo o1, BootstrapInfo o2) {
                if (o1.getOrder() > o2.getOrder()) {
                    return 1;
                }

                if (o1.getOrder() < o2.getOrder()) {
                    return -1;
                }
                return 0;
            }
        });

        return bootstrapInfos;
    }
}
