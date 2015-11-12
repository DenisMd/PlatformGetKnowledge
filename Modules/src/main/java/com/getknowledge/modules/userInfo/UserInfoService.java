package com.getknowledge.modules.userInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.getknowledge.modules.dictonaries.language.Language;
import com.getknowledge.modules.dictonaries.language.LanguageRepository;
import com.getknowledge.modules.dictonaries.language.names.Languages;
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
import com.getknowledge.platform.modules.task.Task;
import com.getknowledge.platform.modules.task.TaskRepository;
import com.getknowledge.platform.modules.task.enumerations.TaskStatus;
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

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LanguageRepository languageRepository;

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
            userInfo.setLanguage(languageRepository.getSingleEntityByFieldAndValue(Language.class,"name", Languages.Ru.name()));
            userInfo.setSpecialty("main admin");
            userInfo.setMan(true);
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
        return userInfo;
    }

    @Action(name = "register" , mandatoryFields = {"email" , "password" , "firstName" , "lastName" , "sex" , "language"})
    public RegisterResult register(HashMap<String,Object> data) {
        String login = (String) data.get("email");
        String password = (String) data.get("password");
        if (password.length() < 6) {
            trace.log("Password less than 6 character for user " + login, TraceLevel.Event);
            return RegisterResult.PasswordLessThan6;
        }

        Language language = languageRepository.getSingleEntityByFieldAndValue(Language.class,"name" , data.get("language"));
        if (language==null) {
            trace.log("Language not supported " + data.get("language"), TraceLevel.Event);
            return RegisterResult.LanguageNotSupported;
        }
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        Boolean sex = (Boolean) data.get("sex");
        if (userRepository.getSingleEntityByFieldAndValue(User.class , "login", login) != null) {
            trace.log("User with email already register " + login, TraceLevel.Event);
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
        userInfo.setLanguage(language);
        userInfo.setFirstName(firstName);
        userInfo.setLastName(lastName);
        userInfo.setMan(sex);

        InputStream is = null;
        if (sex) {
            is = getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/image/male.png");
        } else {
            is = getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/image/female.png");
        }

        try {
            userInfo.setProfileImage(org.apache.commons.io.IOUtils.toByteArray(is));
        } catch (IOException e) {
            trace.logException("Error load file: " + e.getMessage(), e, TraceLevel.Warning);
        }

        userInfoRepository.create(userInfo);

        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setUserInfo(userInfo);
        registerInfo.setCalendar(Calendar.getInstance());
        registerInfo.setUuid(UUID.randomUUID().toString());
        registerInfoRepository.create(registerInfo);

        try {
            emailService.sendTemplate(login,"markovdenis2013@gmail.com", "Регистрация на getKnowledge();",
                    "register",new String[] {registerInfo.getUuid()});
        } catch (Exception e) {
            trace.logException("Error send register email to " + login , e , TraceLevel.Error);
        }

        RegisterResult registerResult = RegisterResult.Complete;
        registerResult.setUserInfoId(userInfo.getId());
        trace.log("Registration complete for user " + login, TraceLevel.Event);

        try {
            Task task = new Task();
            task.setServiceName("RegisterInfoService");
            task.setTaskName("cancelRegistration");
            task.setJsonData(objectMapper.writeValueAsString(registerInfo));
            task.setTaskStatus(TaskStatus.NotStarted);
            //next day
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE , 1);
            task.setStartDate(calendar);
            taskRepository.create(task);
        } catch (JsonProcessingException e) {
            trace.logException("Can't parse register info to josn" , e , TraceLevel.Warning);
        }



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
