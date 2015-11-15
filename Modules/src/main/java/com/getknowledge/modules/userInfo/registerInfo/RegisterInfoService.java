package com.getknowledge.modules.userInfo.registerInfo;

import com.getknowledge.modules.userInfo.results.RegisterResult;
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


@Service("RegisterInfoService")
public class RegisterInfoService extends AbstractService {

    @Autowired
    private RegisterInfoRepository registerInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraceService trace;

    @Action(name = "completeRegistration", mandatoryFields = {"uuid"})
    public RegisterResult completeRegistration(HashMap<String , Object> data) {
        String uuid = (String) data.get("uuid");
        RegisterInfo registerInfo = registerInfoRepository.getSingleEntityByFieldAndValue("uuid", uuid);
        if (registerInfo == null) return null;
        User user = registerInfo.getUserInfo().getUser();
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
    public void cancelRegistration(HashMap<String , Object> data) {
        RegisterInfo registerInfo = registerInfoRepository.getSingleEntityByFieldAndValue("uuid", data.get("uuid").toString());
        if (!registerInfo.getUserInfo().getUser().isEnabled()) {
            trace.log("Cancel registration for user " + registerInfo.getUserInfo().getUser().getLogin() , TraceLevel.Event);
            registerInfoRepository.remove(registerInfo.getId());
        }
    }

}
