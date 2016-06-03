package com.getknowledge.modules.dictionaries.knowledge;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service("KnowledgeService")
public class KnowledgeService extends AbstractService implements ImageService {

    @Autowired
    private KnowledgeRepository knowledgeRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private TraceService trace;

    @ActionWithFile(name = "uploadImage" , mandatoryFields = "knowledgeId")
    @Transactional
    public Result uploadImage(HashMap<String,Object> data, List<MultipartFile> files) {
        Long knowledgeId = longFromField("knowledgeId",data);
        Knowledge knowledge = knowledgeRepository.read(knowledgeId);
        if (knowledge == null) {
            return Result.NotFound();
        }

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        if (!isAccessToEdit(data,knowledge)) {
            return Result.AccessDenied();
        }

        try {
            knowledge.setImage(files.get(0).getBytes());
        } catch (IOException e) {
            trace.logException("Error upload image for knowledge" , e, TraceLevel.Error,true);
            return Result.Failed();
        }

        knowledgeRepository.merge(knowledge);

        return Result.Complete();
    }

    @Override
    public byte[] getImageById(long id) {
        Knowledge knowledge = knowledgeRepository.read(id);
        return knowledge == null ? null : knowledge.getImage();
    }
}
