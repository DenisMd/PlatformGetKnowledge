package com.getknowledge.modules.userInfo.restore.password;

import com.getknowledge.modules.userInfo.results.RegisterResult;
import com.getknowledge.modules.userInfo.results.Result;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.Task;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service("RestorePasswordInfoService")
public class RestorePasswordInfoService extends AbstractService {

    @Autowired
    private RestorePasswordInfoRepository restorePasswordInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraceService trace;

    @Action(name = "restorePassword", mandatoryFields = {"uuid" , "password"})
    public Result completeRegistration(HashMap<String , Object> data) {
        String uuid = (String) data.get("uuid");
        RestorePasswordInfo restorePasswordInfo = restorePasswordInfoRepository.getSingleEntityByFieldAndValue("uuid", uuid);
        if (restorePasswordInfo == null) return Result.Failed;
        User user = restorePasswordInfo.getUserInfo().getUser();
        user.setHashPwd((String) data.get("password"));
        userRepository.update(user);
        restorePasswordInfoRepository.remove(restorePasswordInfo.getId());
        return Result.Complete;
    }

    @Task(name = "removeRestorePasswordInfo")
    public void removeRestorePasswordInfo(HashMap<String , Object> data) {
        RestorePasswordInfo restorePasswordInfo = restorePasswordInfoRepository.getSingleEntityByFieldAndValue("uuid", data.get("uuid").toString());
        if (restorePasswordInfo == null) return;
        trace.log("Remove old restore password" + restorePasswordInfo.getUuid() , TraceLevel.Event);
        restorePasswordInfoRepository.remove(restorePasswordInfo.getId());
    }

}
