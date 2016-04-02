package com.getknowledge.modules.userInfo.courseInfo.tutorialInfo;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("TutorialInfoRepository")
public class TutorialInfoRepository extends BaseRepository<TutorialInfo> {
    @Override
    protected Class<TutorialInfo> getClassEntity() {
        return TutorialInfo.class;
    }
}
