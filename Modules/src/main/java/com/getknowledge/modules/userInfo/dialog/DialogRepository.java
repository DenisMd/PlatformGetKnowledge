package com.getknowledge.modules.userInfo.dialog;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import org.springframework.stereotype.Repository;

@Repository("DialogRepository")
public class DialogRepository extends BaseRepository<Dialog> {
    @Override
    protected Class<Dialog> getClassEntity() {
        return Dialog.class;
    }

    @Override
    public void remove(Dialog entity) {
        super.remove(entity);
    }
}
