package com.getknowledge.platform.modules.bootstrapInfo;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.modules.bootstrapInfo.states.BootstrapState;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("BootstrapInfoRepository")
public class BootstrapInfoRepository extends BaseRepository<BootstrapInfo> {

    @Transactional
    public BootstrapInfo createIfNotExist(BootstrapInfo object) {
        if (object.getName() == null) return null;
        BootstrapInfo bootstrapInfo = getSingleEntityByFieldAndValue(BootstrapInfo.class , "name" , object.getName());
        if (bootstrapInfo != null) return bootstrapInfo;
        object.setBootstrapState(BootstrapState.NotComplete);
        super.create(object);
        return object;
    }
}
