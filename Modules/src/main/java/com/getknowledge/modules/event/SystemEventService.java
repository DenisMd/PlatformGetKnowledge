package com.getknowledge.modules.event;

import com.getknowledge.modules.userInfo.results.RegisterResult;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.Task;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service("SystemEventService")
public class SystemEventService extends AbstractService {

    @Autowired
    private SystemEventRepository systemEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraceService trace;

    @Action(name = "completeRegistration", mandatoryFields = {"uuid"})
    @Transactional
    public RegisterResult completeRegistration(HashMap<String , Object> data) {
        String uuid = (String) data.get("uuid");
        SystemEvent registerInfo = systemEventRepository.getSingleEntityByFieldAndValue("uuid", uuid);
        if (registerInfo == null || registerInfo.getSystemEventType() != SystemEventType.Register) return RegisterResult.NotFound;
        User user = userRepository.read(registerInfo.getUserInfo().getUser().getId());
        if (!user.isEnabled()) {
            user.setEnabled(true);
            userRepository.merge(user);
            RegisterResult registerResult = RegisterResult.Complete;
            registerResult.setUserInfoId(registerInfo.getUserInfo().getId());
            return registerResult;
        } else {
            return RegisterResult.AlreadyActivate;
        }
    }

    @Task(name = "cancelRegistration")
    public void cancelRegistration(HashMap<String , Object> data) throws PlatformException {
        SystemEvent registerInfo = systemEventRepository.getSingleEntityByFieldAndValue("uuid", data.get("uuid").toString());
        if (!registerInfo.getUserInfo().getUser().isEnabled()) {
            trace.log("Cancel registration for user " + registerInfo.getUserInfo().getUser().getLogin() , TraceLevel.Event);
            systemEventRepository.removeWithUser(registerInfo.getId());
        } else {
            systemEventRepository.remove(registerInfo.getId());
        }
    }

    @Action(name = "restorePassword", mandatoryFields = {"uuid" , "password"})
    public Result restorePassword(HashMap<String , Object> data) throws PlatformException {
        String uuid = (String) data.get("uuid");
        SystemEvent restorePasswordInfo = systemEventRepository.getSingleEntityByFieldAndValue("uuid", uuid);
        if (restorePasswordInfo == null || restorePasswordInfo.getSystemEventType() != SystemEventType.RestorePassword) return Result.Failed();
        User user = userRepository.read(restorePasswordInfo.getUserInfo().getUser().getId());
        String password = (String) data.get("password");
        user.hashRawPassword(password);
        userRepository.merge(user);
        systemEventRepository.remove(restorePasswordInfo.getId());
        return Result.Complete();
    }

    @Task(name = "removeRestorePasswordInfo")
    public void removeRestorePasswordInfo(HashMap<String , Object> data) throws PlatformException {
        SystemEvent restorePasswordInfo = systemEventRepository.getSingleEntityByFieldAndValue("uuid", data.get("uuid").toString());
        if (restorePasswordInfo == null) return;
        trace.log("Remove old restore password" + restorePasswordInfo.getUuid() , TraceLevel.Event);
        systemEventRepository.remove(restorePasswordInfo.getId());
    }

}
