package com.getknowledge.modules.userInfo;

import com.getknowledge.modules.email.EmailService;
import com.getknowledge.modules.userInfo.registerInfo.RegisterInfo;
import com.getknowledge.modules.userInfo.registerInfo.RegisterInfoRepository;
import com.getknowledge.modules.userInfo.results.RegisterResult;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.names.RoleName;
import com.getknowledge.platform.modules.role.RoleRepository;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.*;

@Service("UserInfoService")
public class UserInfoService extends AbstractService implements BootstrapService,ImageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RegisterInfoRepository registerInfoRepository;

    @Autowired
    private TraceService trace;

    @Override
    public void bootstrap(HashMap<String, Object> map) {
        if(userRepository.count(User.class) == 0) {
            String login = "admin";
            String password = "admin";
            String lastName = "Markov";
            String firstName = "Denis";

            if (map.containsKey("login")) {
                login = (String) map.get("login");
            }

            if (map.containsKey("password")) {
                password = (String) map.get("password");
            }
            if(map.containsKey("lastName")) {
                lastName = (String) map.get("lastName");
            }
            if (map.containsKey("firstName")) {
                firstName = (String) map.get("firstName");
            }

            Role role = roleRepository.getSingleEntityByFieldAndValue(Role.class, "roleName" , RoleName.ROLE_ADMIN.name());
            if (role == null) {
                return;
            }

            User user = new User();
            user.setLogin(login);
            user.setHashPwd(password);
            user.setRole(role);
            user.setEnabled(true);
            user.setPwdTransient(password);
            userRepository.create(user);

            UserInfo userInfo = new UserInfo();
            userInfo.setUser(user);
            userInfo.setFirstName(firstName);
            userInfo.setLastName(lastName);
            userInfo.setLanguage("ru");
            userInfo.setSpecialty("main admin");
            InputStream is = getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/image/photo.png");
            try {
                userInfo.setProfileImage(org.apache.commons.io.IOUtils.toByteArray(is));
            } catch (IOException e) {
                trace.logException("Error load file: " + e.getMessage(), e, TraceLevel.Warning);
            }
            userInfoRepository.create(userInfo);
        }
    }

    @Action(name = "getAuthorizedUser")
    public UserInfo getAuthorizedUser(HashMap<String,Object> data){
        String login = (String) data.get("principalName");
        if (login == null) {return  null;}

        User user = userRepository.getSingleEntityByFieldAndValue(User.class, "login", login);
        userInfoRepository.setCurrentUser(user);
        UserInfo userInfo = userInfoRepository.getSingleEntityByFieldAndValue(UserInfo.class,"user.login",login);
        userInfo.setFirstName("Wiiii");
        return userInfo;
    }

    @Action(name = "register" , mandatoryFields = {"login" , "password" , "firstName" , "lastName"})
    public RegisterResult register(HashMap<String,Object> data) {
        String login = (String) data.get("login");
        String password = (String) data.get("password");
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        if (userRepository.getSingleEntityByFieldAndValue(User.class , "login", login) != null) {
            return RegisterResult.UserAlreadyCreated;
        }

        User user = new User();
        user.setLogin(login);
        user.setPwdTransient(password);
        user.setEnabled(false);
        user.setRole(roleRepository.getSingleEntityByFieldAndValue(Role.class, "roleName", RoleName.ROLE_USER.name()));
        userRepository.create(user);
        UserInfo userInfo = new UserInfo();
        userInfo.setUser(user);
        userInfo.setFirstName(firstName);
        userInfo.setLastName(lastName);
        userInfoRepository.create(userInfo);

        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setUserInfo(userInfo);
        registerInfo.setCalendar(Calendar.getInstance());
        registerInfo.setUuid(UUID.randomUUID().toString());
        registerInfoRepository.create(registerInfo);

        emailService.send("markovdenis2013@gmail.com", login, "Регистрация на getKnowledge();", registerInfo.getUuid());

        RegisterResult registerResult = RegisterResult.Complete;
        registerResult.setUserInfoId(userInfo.getId());
        return registerResult;
    }

    @ActionWithFile(name = "extraInfo" , mandatoryFields = {"image" , "specialty" , "birth_day"})
    public RegisterResult registerExtraInfo (HashMap<String,Object> data,MultipartFile file) {

        for (Map.Entry<String , Object> entry : data.entrySet()) {
            System.err.println("key " + entry.getKey() + " : value : " + entry.getValue());
        }

        return RegisterResult.Complete;
    }


    public UserInfo getCurrentUser(Principal p) {
        if (p == null) return null;
        UserInfo result = userInfoRepository.getSingleEntityByFieldAndValue(UserInfo.class, "user.login", p.getName());
        return result;
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("User Service");
        bootstrapInfo.setOrder(1);
        return bootstrapInfo;
    }

    @Override
    public byte[] getImageById(long id) {
        UserInfo userInfo = userInfoRepository.read(id , UserInfo.class);
        byte [] bytes = userInfo.getProfileImage();
        return bytes;
    }
}
