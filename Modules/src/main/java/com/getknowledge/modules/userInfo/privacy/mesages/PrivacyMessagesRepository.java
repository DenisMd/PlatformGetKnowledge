package com.getknowledge.modules.userInfo.privacy.mesages;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("PrivacyMessagesRepository")
public class PrivacyMessagesRepository extends BaseRepository<PrivacyMessages> {
    @Override
    protected Class<PrivacyMessages> getClassEntity() {
        return PrivacyMessages.class;
    }
}
