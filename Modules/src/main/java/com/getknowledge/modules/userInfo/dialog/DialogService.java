package com.getknowledge.modules.userInfo.dialog;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.modules.userInfo.dialog.messages.DialogMessage;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service("DialogService")
public class DialogService extends AbstractService {

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Action(name = "getMessagesFromDialog" , mandatoryFields = {"dialogId"})
    public List<DialogMessage> getMessages(HashMap<String,Object> data) throws NotAuthorized {
        Long dialogId = longFromField("dialogId",data);
        Dialog dialog = dialogRepository.read(dialogId);
        if (data == null){
            return null;
        }
        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (!dialog.getUser().equals(userInfo)){
            throw new NotAuthorized("Access denied for read messages");
        }


        return dialog.getMessages();
    }

}
