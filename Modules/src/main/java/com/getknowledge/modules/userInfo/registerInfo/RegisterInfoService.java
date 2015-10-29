package com.getknowledge.modules.userInfo.registerInfo;

import com.getknowledge.modules.userInfo.results.RegisterResult;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
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

    @Action(name = "completeRegistration", mandatoryFields = {"uuid"})
    public RegisterResult completeRegistration(HashMap<String , Object> data) {
        String uuid = (String) data.get("uuid");
        RegisterInfo registerInfo = registerInfoRepository.getSingleEntityByFieldAndValue(RegisterInfo.class , "uuid" , uuid);
        User user = registerInfo.getUserInfo().getUser();
        if (!user.isEnabled()) {
            user.setEnabled(true);
            userRepository.update(user);
        }
        return RegisterResult.RegistrationTimeout;
    }

}
