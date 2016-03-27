package com.getknowledge.modules.userInfo.dialog;

import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.userInfo.dialog.messages.DialogMessage;
import com.getknowledge.modules.userInfo.dialog.messages.DialogMessageRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("DialogRepository")
public class DialogRepository extends BaseRepository<Dialog> {

    @Autowired
    private DialogMessageRepository dialogMessageRepository;

    @Override
    protected Class<Dialog> getClassEntity() {
        return Dialog.class;
    }

    @Override
    public void remove(Dialog entity) {
        for (DialogMessage dialogMessage : entity.getMessages()) {
            dialogMessage.getDialogs().remove(entity);
            dialogMessageRepository.merge(dialogMessage);
            if (dialogMessage.getDialogs().isEmpty()){
                dialogMessageRepository.remove(dialogMessage);
            }
        }

        super.remove(entity);
    }
}
