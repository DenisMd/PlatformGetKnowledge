package com.getknowledge.modules.event;

import com.getknowledge.modules.userInfo.results.RegisterResult;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.Task;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("UserEventService")
public class UserEventService extends AbstractService {

    @Autowired
    private UserEventRepository userEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraceService trace;

    @Action(name = "completeRegistration", mandatoryFields = {"uuid"})
    public RegisterResult completeRegistration(HashMap<String , Object> data) {
        String uuid = (String) data.get("uuid");
        UserEvent registerInfo = userEventRepository.getSingleEntityByFieldAndValue("uuid", uuid);
        if (registerInfo == null || registerInfo.getUserEventType() != UserEventType.Register) return RegisterResult.NotFound;
        User user = userRepository.read(registerInfo.getUserInfo().getUser().getId());
        if (!user.isEnabled()) {
            user.setEnabled(true);
            userRepository.update(user);
            RegisterResult registerResult = RegisterResult.Complete;
            registerResult.setUserInfoId(registerInfo.getUserInfo().getId());
            return registerResult;
        } else {
            return RegisterResult.AlreadyActivate;
        }
    }

    @Task(name = "cancelRegistration")
    public void cancelRegistration(HashMap<String , Object> data) throws PlatformException {
        UserEvent registerInfo = userEventRepository.getSingleEntityByFieldAndValue("uuid", data.get("uuid").toString());
        if (!registerInfo.getUserInfo().getUser().isEnabled()) {
            trace.log("Cancel registration for user " + registerInfo.getUserInfo().getUser().getLogin() , TraceLevel.Event);
            userEventRepository.removeWithUser(registerInfo.getId());
        } else {
            userEventRepository.remove(registerInfo.getId());
        }
    }

    @Action(name = "restorePassword", mandatoryFields = {"uuid" , "password"})
    public Result restorePassword(HashMap<String , Object> data) throws PlatformException {
        String uuid = (String) data.get("uuid");
        UserEvent restorePasswordInfo = userEventRepository.getSingleEntityByFieldAndValue("uuid", uuid);
        if (restorePasswordInfo == null || restorePasswordInfo.getUserEventType() != UserEventType.RestorePassword) return Result.Failed();
        User user = userRepository.read(restorePasswordInfo.getUserInfo().getUser().getId());
        String password = (String) data.get("password");
        user.hashRawPassword(password);
        userRepository.update(user);
        userEventRepository.remove(restorePasswordInfo.getId());
        return Result.Complete();
    }

    @Task(name = "removeRestorePasswordInfo")
    public void removeRestorePasswordInfo(HashMap<String , Object> data) throws PlatformException {
        UserEvent restorePasswordInfo = userEventRepository.getSingleEntityByFieldAndValue("uuid", data.get("uuid").toString());
        if (restorePasswordInfo == null) return;
        trace.log("Remove old restore password" + restorePasswordInfo.getUuid() , TraceLevel.Event);
        userEventRepository.remove(restorePasswordInfo.getId());
    }

}
