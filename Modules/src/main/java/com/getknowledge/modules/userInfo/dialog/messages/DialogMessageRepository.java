package com.getknowledge.modules.userInfo.dialog.messages;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("DialogMessageRepository")
public class DialogMessageRepository extends BaseRepository<DialogMessage> {
    @Override
    protected Class<DialogMessage> getClassEntity() {
        return DialogMessage.class;
    }
}
