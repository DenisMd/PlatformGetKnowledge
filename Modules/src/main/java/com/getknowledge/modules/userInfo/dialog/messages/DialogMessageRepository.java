package com.getknowledge.modules.userInfo.dialog.messages;

import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.messages.attachments.AttachmentImageRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.dialog.Dialog;
import com.getknowledge.modules.userInfo.dialog.DialogRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository("DialogMessageRepository")
public class DialogMessageRepository extends BaseRepository<DialogMessage> {

    @Autowired
    private AttachmentImageRepository attachmentImageRepository;

    @Override
    protected Class<DialogMessage> getClassEntity() {
        return DialogMessage.class;
    }

    @Override
    public void remove(DialogMessage entity) {
        for (AttachmentImage attachmentImage : entity.getImages()) {
            attachmentImageRepository.remove(attachmentImage.getId());
        }
        super.remove(entity);
    }

    public DialogMessage createDialogMessage(UserInfo sender,String text,Dialog d1,Dialog d2){
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.setMessage(text);
        dialogMessage.setCreateTime(Calendar.getInstance());
        dialogMessage.setSender(sender);
        dialogMessage.getDialogs().add(d1);
        dialogMessage.getDialogs().add(d2);
        create(dialogMessage);
        return dialogMessage;
    }
}
