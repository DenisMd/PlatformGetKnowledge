package com.getknowledge.modules.help.desc;

import com.getknowledge.platform.base.serializers.FileResponse;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.modules.help.desc.attachements.FileAttachment;
import com.getknowledge.modules.help.desc.type.HpMessageType;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.FileService;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service("HPMessageService")
public class HPMessageService extends AbstractService implements FileService {

    @Autowired
    private TraceService trace;

    @Autowired
    private HPMessageRepository hpRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Action(name = "sendHpMessage" , mandatoryFields = {"title" , "message", "type"})
    public Result sendHpMessage(HashMap<String,Object> data) {

        try {
            String title = (String) data.get("title");
            String message = (String) data.get("message");
            HpMessageType messageType = HpMessageType.valueOf((String) data.get("type"));

            HpMessage hpMessage = new HpMessage();
            hpMessage.setTitle(title);
            hpMessage.setMessage(message);
            hpMessage.setType(messageType);

            if (data.containsKey("principal")) {
                User user = userInfoService.getAuthorizedUser(data).getUser();
                hpMessage.setUser(user);
                if (data.containsKey("isReply")) {
                    hpMessage.setReply((Boolean) data.get("isReply"));
                }
            }

            hpRepository.create(hpMessage);

        } catch (Exception e) {
            trace.logException("Exception for sendHpMessage" , e, TraceLevel.Warning);
            return Result.Failed();
        }
        return Result.Complete();
    }

    @ActionWithFile(name = "sendHpMessage" , mandatoryFields = {"hpMessageId"})
    public Result sendHpMessageWithAttachFiles(HashMap<String,Object> data , List<MultipartFile> list) {

        long id = new Long((int) data.get("hpMessageId"));
        HpMessage hpMessage = hpRepository.read(id);

        //save attach files
        for (MultipartFile file : list) {
            try {
                FileAttachment fileAttachment = new FileAttachment();
                fileAttachment.setData(file.getBytes());
                fileAttachment.setMessage(hpMessage);
                entityManager.persist(fileAttachment);
                entityManager.flush();
            } catch (IOException e) {
                trace.logException("Error get attach file",e,TraceLevel.Warning);
                return Result.Failed();
            }
        }

        hpRepository.merge(hpMessage);
        return Result.Complete();
    }

    @Override
    public FileResponse getFile(long id, Object key) {
        HpMessage message = hpRepository.read(id);
        if (message == null)
            return null;

        long fileId = Long.parseLong(String.valueOf(key));

        Optional<FileAttachment> result = message.getFiles().stream().filter((file) -> file.getId() == fileId).findFirst();

        FileResponse fileResponse = new FileResponse();
        if (result.isPresent()) {
            fileResponse.setData(result.get().getData());
            fileResponse.setFileName(result.get().getFileName());
        }

        return fileResponse;
    }
}
