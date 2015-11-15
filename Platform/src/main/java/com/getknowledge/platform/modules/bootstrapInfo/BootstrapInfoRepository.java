package com.getknowledge.platform.modules.bootstrapInfo;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.modules.bootstrapInfo.states.BootstrapState;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("BootstrapInfoRepository")
public class BootstrapInfoRepository extends BaseRepository<BootstrapInfo> {

    @Override
    protected Class<BootstrapInfo> getClassEntity() {
        return BootstrapInfo.class;
    }

    @Transactional
    public BootstrapInfo createIfNotExist(BootstrapInfo object) {
        if (object.getName() == null) return null;
        BootstrapInfo bootstrapInfo = getSingleEntityByFieldAndValue("name" , object.getName());
        if (bootstrapInfo != null) return bootstrapInfo;
        object.setBootstrapState(BootstrapState.NotComplete);
        super.create(object);
        return object;
    }
}
