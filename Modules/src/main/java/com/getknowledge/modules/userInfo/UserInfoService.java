package com.getknowledge.modules.userInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.getknowledge.modules.dictionaries.city.City;
import com.getknowledge.modules.dictionaries.city.CityRepository;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.dictionaries.language.LanguageRepository;
import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.modules.email.EmailService;
import com.getknowledge.modules.event.UserEvent;
import com.getknowledge.modules.event.UserEventRepository;
import com.getknowledge.modules.event.UserEventType;
import com.getknowledge.modules.settings.Settings;
import com.getknowledge.modules.settings.SettingsRepository;
import com.getknowledge.modules.userInfo.results.RegisterResult;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.modules.userInfo.socialLink.UserSocialLink;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.RoleRepository;
import com.getknowledge.platform.modules.role.names.RoleName;
import com.getknowledge.platform.modules.task.Task;
import com.getknowledge.platform.modules.task.TaskRepository;
import com.getknowledge.platform.modules.task.enumerations.TaskStatus;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import com.getknowledge.platform.utils.ModuleLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private UserEventRepository userEventRepository;


    @Autowired
    private TraceService trace;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ModuleLocator moduleLocator;

    @Override
    public void bootstrap(HashMap<String, Object> map) {
        if(userRepository.count() == 0) {
            String login = "admin";
            String password = "admin";
            String lastName = "Markov";
            String firstName = "Denis";

            if (map.containsKey("email")) {
                login = (String) map.get("email");
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

            Role role = roleRepository.getSingleEntityByFieldAndValue("roleName" , RoleName.ROLE_ADMIN.name());
            if (role == null) {
                return;
            }

            User user = new User();
            user.setLogin(login);
            user.hashRawPassword(password);
            user.setRole(role);
            user.setEnabled(true);
            user.setPwdTransient(password);
            userRepository.create(user);

            UserInfo userInfo = new UserInfo();
            userInfo.setUser(user);
            userInfo.setFirstName(firstName);
            userInfo.setLastName(lastName);
            userInfo.setLanguage(languageRepository.getSingleEntityByFieldAndValue("name", Languages.Ru.name()));
            userInfo.setSpecialty("main admin");
            userInfo.setMan(true);
            userInfo.setFirstLogin(true);
            InputStream is = getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/image/photo.png");
            try {
                userInfo.setProfileImage(org.apache.commons.io.IOUtils.toByteArray(is));
            } catch (IOException e) {
                trace.logException("Error load file: " + e.getMessage(), e, TraceLevel.Warning);
            }
            userInfoRepository.create(userInfo);
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("User Service");
        bootstrapInfo.setOrder(1);
        return bootstrapInfo;
    }

    @Action(name = "getAuthorizedUser")
    public UserInfo getAuthorizedUser(HashMap<String,Object> data){
        String login = (String) data.get("principalName");
        if (login == null) {return  null;}

        User user = userRepository.getSingleEntityByFieldAndValue("login", login);
        UserInfo userInfo = userInfoRepository.getSingleEntityByFieldAndValue("user.login",login);
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

        Language language = languageRepository.getSingleEntityByFieldAndValue("name" , data.get("language"));
        if (language==null) {
            trace.log("Language not supported " + data.get("language"), TraceLevel.Event);
            return RegisterResult.LanguageNotSupported;
        }
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        Boolean sex = (Boolean) data.get("sex");
        if (userRepository.getSingleEntityByFieldAndValue("login", login) != null) {
            trace.log("User with email already register " + login, TraceLevel.Event);
            return RegisterResult.UserAlreadyCreated;
        }

        String uuid = UUID.randomUUID().toString();
        Settings settings = settingsRepository.getSettings();
        try {
            String url = settings.getDomain() + "/#/"+language.getName().toLowerCase()+"/accept/" + uuid;
            emailService.sendTemplate(login,settings.getEmail(), "Регистрация на getKnowledge();",
                    "register",new String[] {settingsRepository.getSettings().getDomain(),url});
        } catch (Exception e) {
            trace.logException("Error send register email to " + login , e , TraceLevel.Error);
            return RegisterResult.EmailNotSend;
        }

        User user = new User();
        user.setLogin(login);
        user.hashRawPassword(password);
        user.setEnabled(false);
        user.setRole(roleRepository.getSingleEntityByFieldAndValue("roleName", RoleName.ROLE_USER.name()));
        userRepository.create(user);
        UserInfo userInfo = new UserInfo();
        userInfo.setUser(user);
        userInfo.setLanguage(language);
        userInfo.setFirstName(firstName);
        userInfo.setLastName(lastName);
        userInfo.setMan(sex);
        userInfo.setFirstLogin(true);

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

        UserEvent registerInfo = new UserEvent();
        registerInfo.setUserInfo(userInfo);
        registerInfo.setCalendar(Calendar.getInstance());
        registerInfo.setUuid(uuid);
        registerInfo.setUserEventType(UserEventType.Register);
        userEventRepository.create(registerInfo);

        RegisterResult registerResult = RegisterResult.Complete;
        registerResult.setUserInfoId(userInfo.getId());
        trace.log("Registration complete for user " + login, TraceLevel.Event);

        try {
            Task task = new Task();
            task.setServiceName("UserEventService");
            task.setTaskName("cancelRegistration");
            task.setJsonData(objectMapper.writeValueAsString(registerInfo));
            task.setTaskStatus(TaskStatus.NotStarted);
            //next day
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE , 1);
            task.setStartDate(calendar);
            taskRepository.create(task);
        } catch (JsonProcessingException e) {
            trace.logException("Can't parse register info to json" , e , TraceLevel.Warning);
        }



        return registerResult;
    }

    @ActionWithFile(name = "updateImage")
    public Result updateImage (HashMap<String,Object> data,List<MultipartFile> files) throws PlatformException {
        UserInfo userInfo = getAuthorizedUser(data);

        if (userInfo == null) return Result.SessionFailed();

        if (files != null) {
            try {
                userInfo.setProfileImage(files.get(0).getBytes());
            } catch (IOException e) {
                trace.logException("Error get bytes for image", e, TraceLevel.Error);
            }
        }

        userInfoRepository.update(userInfo);

        return Result.Complete();
    }

    @Action(name = "updateExtraInfo" , mandatoryFields = {"userId"})
    public RegisterResult registerExtraInfo (HashMap<String,Object> data) throws PlatformException {

        Long id = new Long((Integer)data.get("userId"));

        UserInfo userInfo = getAuthorizedUser(data);

        if (!userInfo.getId().equals(id)) {
            throw new NotAuthorized("User id is not correct" , trace, TraceLevel.Event);
        }

        if (data.containsKey("cityId")) {
            Long cityId = new Long((Integer)data.get("cityId"));
            City city = cityRepository.read(cityId);
            if (city != null) {
                userInfo.setCity(city);
            }
        }

        if (data.containsKey("speciality")) {
            userInfo.setSpecialty((String) data.get("speciality"));
        }

        if (data.containsKey("date")) {
            try {
                Date date = new SimpleDateFormat("dd.MM.yyyy").parse((String) data.get("date"));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                userInfo.setBirthDay(calendar);
            } catch (ParseException e) {
                trace.logException("Error date format : " + (String) data.get("date"), e, TraceLevel.Event);
            }
        }

        userInfo.setFirstLogin(false);

        userInfoRepository.update(userInfo);

        return RegisterResult.Complete;
    }

    @Action(name = "skipExtraRegistration")
    public Result skipExtraRegistration(HashMap<String,Object> data) {
        UserInfo userInfo = getAuthorizedUser(data);
        if (userInfo == null) return Result.SessionFailed();
        userInfo.setFirstLogin(false);
        userInfoRepository.update(userInfo);
        return Result.Complete();
    }

    @Action(name = "updateStatus" , mandatoryFields = "status")
    public Result updateStatus (HashMap<String , Object> data) {
        UserInfo userInfo = getAuthorizedUser(data);
        if (userInfo == null) return Result.SessionFailed();
        userInfo.setStatus((String) data.get("status"));
        userInfoRepository.update(userInfo);
        return Result.Complete();
    }

    @Action(name = "setLinks")
    public Result setLinks(HashMap<String , Object> data) {
        UserInfo userInfo = getAuthorizedUser(data);
        if (userInfo == null) return Result.SessionFailed();

        if (data.containsKey("webSite")) {
            String webSite = (String) data.get("webSite");
            userInfo.setWebSite(webSite);
        }

        String vk = "";
        String facebook = "";
        String github = "";
        String twitter = "";

        UserSocialLink userSocialLink = userInfo.getLinks();
        if (userInfo == null) userSocialLink = new UserSocialLink();

        if (data.containsKey("vkLink")) {
            vk = (String) data.get("vkLink");
            userSocialLink.setVkLink(vk);
        }

        if (data.containsKey("facebookLink")) {
            facebook = (String) data.get("facebookLink");
            userSocialLink.setFacebookLink(facebook);
        }

        if (data.containsKey("githubLink")) {
            github = (String) data.get("githubLink");
            userSocialLink.setGithubLink(github);
        }

        if (data.containsKey("twitterLink")) {
            twitter = (String) data.get("twitterLink");
            userSocialLink.setTwitterLink(twitter);
        }

        userInfo.setLinks(userSocialLink);
        userInfoRepository.update(userInfo);

        return Result.Complete();
    }

    @Action(name = "forgotPassword" , mandatoryFields = {"email"})
    public Result forgotPassword(HashMap<String , Object> data) {
        UserInfo userInfo = userInfoRepository.getSingleEntityByFieldAndValue("user.login" , data.get("email"));
        if (userInfo == null || !userInfo.getUser().isEnabled())
            return Result.Failed();

        String uuid = UUID.randomUUID().toString();

        Settings settings = settingsRepository.getSettings();
        try {
            String url = settings.getDomain() + "/#/"+userInfo.getLanguage().getName().toLowerCase()+"/restorePassword/" + uuid;
            emailService.sendTemplate(userInfo.getUser().getLogin(),settings.getEmail(), "Востановление пароля на getKnowledge();",
                    "forgotPassword",new String[] {settingsRepository.getSettings().getDomain(),url});
        } catch (Exception e) {
            trace.logException("Error send register email to " + userInfo.getUser().getLogin() , e , TraceLevel.Error);
            return Result.EmailNotSend();
        }

        UserEvent restorePasswordInfo = new UserEvent();
        restorePasswordInfo.setUserInfo(userInfo);
        restorePasswordInfo.setCalendar(Calendar.getInstance());
        restorePasswordInfo.setUuid(uuid);
        restorePasswordInfo.setUserEventType(UserEventType.RestorePassword);
        userEventRepository.create(restorePasswordInfo);

        try {
            Task task = new Task();
            task.setServiceName("UserEventService");
            task.setTaskName("removeRestorePasswordInfo");
            task.setJsonData(objectMapper.writeValueAsString(restorePasswordInfo));
            task.setTaskStatus(TaskStatus.NotStarted);
            //next day
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE , 2);
            task.setStartDate(calendar);
            taskRepository.create(task);
        } catch (JsonProcessingException e) {
            trace.logException("Can't parse restore password info to json" , e , TraceLevel.Warning);
        }

        return Result.Complete();
    }

    public UserInfo getCurrentUser(Principal p) {
        if (p == null) return null;
        UserInfo result = userInfoRepository.getSingleEntityByFieldAndValue("user.login", p.getName());
        return result;
    }

    @Override
    public byte[] getImageById(long id) {
        UserInfo userInfo = userInfoRepository.read(id);
        byte [] bytes = userInfo.getProfileImage();
        return bytes;
    }
}
