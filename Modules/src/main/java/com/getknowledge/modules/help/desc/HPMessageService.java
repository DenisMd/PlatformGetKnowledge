package com.getknowledge.modules.help.desc;

import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("HPMessageService")
public class HPMessageService extends AbstractService {

    @Action(name = "sendHpMessage" , mandatoryFields = {"title" , "message", "type"})
    public String sendHpMessage(HashMap<String,Object> data) {
        return "";
    }

    @ActionWithFile(name = "sendHpMessage" , mandatoryFields = {"title" , "message", "type"})
    public String sendHpMessageWithAttachFiles(HashMap<String,Object> data) {

        //save attach files

        return sendHpMessage(data);
    }

}
