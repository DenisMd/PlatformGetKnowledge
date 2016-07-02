package com.getknowledge.modules.userInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.getknowledge.modules.dictionaries.city.City;
import com.getknowledge.modules.dictionaries.city.CityRepository;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.dictionaries.language.LanguageRepository;
import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.modules.email.EmailService;
import com.getknowledge.modules.email.EmailTemplates;
import com.getknowledge.modules.event.SystemEvent;
import com.getknowledge.modules.event.SystemEventRepository;
import com.getknowledge.modules.event.SystemEventType;
import com.getknowledge.modules.event.user.UserEvent;
import com.getknowledge.modules.event.user.UserEventRepository;
import com.getknowledge.modules.event.user.UserEventType;
import com.getknowledge.modules.settings.Settings;
import com.getknowledge.modules.settings.SettingsRepository;
import com.getknowledge.modules.userInfo.dialog.Dialog;
import com.getknowledge.modules.userInfo.dialog.DialogRepository;
import com.getknowledge.modules.userInfo.dialog.messages.DialogMessage;
import com.getknowledge.modules.userInfo.dialog.messages.DialogMessageRepository;
import com.getknowledge.modules.userInfo.post.messages.PostMessage;
import com.getknowledge.modules.userInfo.post.messages.PostMessageRepository;
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
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import com.getknowledge.platform.utils.ModuleLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private SystemEventRepository systemEventRepository;


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
    private UserEventRepository userEventRepository;

    @Autowired
    private PostMessageRepository postMessageRepository;

    @Autowired
    private DialogMessageRepository dialogMessageRepository;

    @Autowired
    private DialogRepository dialogRepository;

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

            Role role = roleRepository.getRole(RoleName.ROLE_ADMIN);

            User user = userRepository.createUser(login,password,role,true);


            byte [] profileImage = null;
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/image/adminAvatar.png");) {
                profileImage = org.apache.commons.io.IOUtils.toByteArray(is);
            } catch (IOException e) {
                trace.logException("Error load file: " + e.getMessage(), e, TraceLevel.Error,true);
            }

            userInfoRepository.createUserInfo(user,firstName,lastName,languageRepository.getLanguage(Languages.Ru),true,profileImage);
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("User service");
        bootstrapInfo.setOrder(1);
        return bootstrapInfo;
    }

    @Action(name = "getAuthorizedUser")
    @Transactional
    public UserInfo getAuthorizedUser(HashMap<String,Object> data) {
        return userInfoRepository.getCurrentUser(data);
    }

    @Action(name = "register" , mandatoryFields = {"email" , "password" , "firstName" , "lastName" , "sex" , "language"})
    @Transactional
    public RegisterResult register(HashMap<String,Object> data) throws IOException {
        String login = (String) data.get("email");
        String password = (String) data.get("password");
        if (password.length() < 6) {
            trace.log("Password less than 6 character for user " + login, TraceLevel.Event,false);
            return RegisterResult.PasswordLessThan6;
        }

        Language language = languageRepository.getSingleEntityByFieldAndValue("name" , data.get("language"));
        if (language==null) {
            trace.log("Language not supported " + data.get("language"), TraceLevel.Event,false);
            return RegisterResult.LanguageNotSupported;
        }
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        Boolean sex = (Boolean) data.get("sex");
        if (userRepository.getSingleEntityByFieldAndValue("login", login) != null) {
            trace.log("User with email already register " + login, TraceLevel.Event,false);
            return RegisterResult.UserAlreadyCreated;
        }

        String uuid = null;
        try {
            uuid = UUID.randomUUID().toString();
            Settings settings = settingsRepository.getSettings();
            String url = settings.getDomain() + "/#/"+language.getName().toLowerCase()+"/accept/" + uuid;
            emailService.sendTemplate(login,settings.getEmail(), "Регистрация на getKnowledge();",
                    EmailTemplates.Register,new String[] {settingsRepository.getSettings().getDomain(),url});
        } catch (Exception e) {
            trace.logException("Error send register email to " + login , e , TraceLevel.Error,true);
            return RegisterResult.EmailNotSend;
        }


        User user = userRepository.createUser(login,password,roleRepository.getRole(RoleName.ROLE_USER),false);
        byte [] profileImage = null;

        InputStream is = null;
        try {
            if (sex) {
                is = getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/image/male.png");
            } else {
                is = getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/image/female.png");
            }
            profileImage = org.apache.commons.io.IOUtils.toByteArray(is);
        } catch (IOException e) {
            trace.logException("Error load file: " + e.getMessage(), e, TraceLevel.Warning,true);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        UserInfo userInfo = userInfoRepository.createUserInfo(user,firstName,lastName,language,sex,profileImage);


        SystemEvent systemEvent = systemEventRepository.createSystemEvent(userInfo,uuid,SystemEventType.Register);

        RegisterResult registerResult = RegisterResult.Complete;
        registerResult.setUserInfoId(userInfo.getId());
        trace.log("Registration complete for user " + login, TraceLevel.Event,true);

        try {
            String jsonData = objectMapper.writeValueAsString(systemEvent);
            //next day
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE , 1);
            taskRepository.createTask("SystemEventService","cancelRegistration",jsonData,calendar);
        } catch (JsonProcessingException e) {
            trace.logException("Can't parse register info to json" , e , TraceLevel.Warning,true);
        }

        return registerResult;
    }

    @ActionWithFile(name = "updateImage")
    @Transactional
    public Result updateImage (HashMap<String,Object> data,List<MultipartFile> files) throws PlatformException {

        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null) return Result.NotAuthorized();

        try {
            userInfo.setProfileImage(files.get(0).getBytes());
        } catch (IOException e) {
            trace.logException("Error get bytes for image", e, TraceLevel.Error,true);
        }

        userInfoRepository.merge(userInfo);
        return Result.Complete();
    }

    @Action(name = "updateExtraInfo")
    @Transactional
    public Result updateExtraInfo (HashMap<String,Object> data) throws PlatformException {

        UserInfo userInfo = userInfoRepository.getCurrentUser(data);

        if (userInfo == null) {
            return Result.NotAuthorized();
        }

        if (data.containsKey("cityId")) {
            Long cityId = longFromField("cityId",data);
            City city = cityRepository.read(cityId);
            if (city != null) {
                userInfo.setCity(city);
            }
        }

        if (data.containsKey("speciality")) {
            userInfo.setSpeciality((String) data.get("speciality"));
        }

        if (data.containsKey("date")) {
            Date date = new Date(longFromField("date" , data));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            userInfo.setBirthDay(calendar);
        }

        userInfo.setFirstLogin(false);
        userInfoRepository.merge(userInfo);
        return Result.Complete();
    }

    @Action(name = "getFriends" , mandatoryFields = {"userId"})
    @Transactional
    public List<UserInfo> getFriends(HashMap<String,Object> data) {
        Long userId = this.longFromField("userId", data);
        UserInfo userInfo = userInfoRepository.read(userId);
        if (userInfo == null) return null;
        return userInfo.getFriends();
    }

    @Action(name = "addFriend" , mandatoryFields = {"friendId"})
    @Transactional
    public Result addFriend(HashMap<String,Object> data) {
        UserInfo friend = userInfoRepository.read(longFromField("friendId", data));
        if (friend == null) {
            return Result.NotFound();
        }

        UserInfo i = userInfoRepository.getCurrentUser(data);
        if (i == null) return Result.NotAuthorized();

        userEventRepository.createUserEvent(friend,i.getId().toString(),UserEventType.FriendRequest);
        return Result.Complete();
    }

    @Action(name = "removeFriend" , mandatoryFields = {"friendId"})
    @Transactional
    public Result removeFriend(HashMap<String,Object> data) {
        UserInfo i = getAuthorizedUser(data);
        if (i == null) return Result.NotAuthorized();

        UserInfo friend = userInfoRepository.read(longFromField("friendId", data));
        if (friend == null) {
            return Result.NotFound();
        }

        i.getFriends().remove(friend);
        userInfoRepository.merge(i);

        friend.getFriends().remove(i);
        userInfoRepository.merge(friend);

        userEventRepository.createUserEvent(friend,i.getId().toString(),UserEventType.FriendRemove);
        return Result.Complete();
    }


    @Action(name = "acceptFriend" , mandatoryFields = {"eventId","accept"})
    @Transactional
    public Result acceptFriend(HashMap<String,Object> data){
        UserInfo i = getAuthorizedUser(data);
        if (i == null) return Result.NotAuthorized();

        UserEvent userEvent = userEventRepository.read(longFromField("eventId", data));
        if (userEvent == null || userEvent.getOwner().getId() != i.getId()) return Result.AccessDenied();

        boolean accept = (boolean) data.get("accept");

        if (accept) {
            UserInfo friend = userInfoRepository.read(Long.parseLong(userEvent.getData()));
            i.getFriends().add(friend);
            userInfoRepository.merge(i);
            friend.getFriends().add(i);
            userInfoRepository.merge(friend);
        }

        userEvent.setChecked(true);
        userEventRepository.merge(userEvent);
        return Result.Complete();
    }

    @Action(name = "updateStatus" , mandatoryFields = "status")
    @Transactional
    public Result updateStatus (HashMap<String , Object> data) {
        UserInfo userInfo = getAuthorizedUser(data);
        if (userInfo == null) return Result.NotAuthorized();
        userInfo.setStatus((String) data.get("status"));
        userInfoRepository.merge(userInfo);
        return Result.Complete();
    }

    @Action(name = "setLinks")
    @Transactional
    public Result setLinks(HashMap<String , Object> data) {
        UserInfo userInfo = getAuthorizedUser(data);
        if (userInfo == null) return Result.NotAuthorized();

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
        userInfoRepository.merge(userInfo);
        return Result.Complete();
    }

    @Action(name = "forgotPassword" , mandatoryFields = {"email"})
    @Transactional
    public Result forgotPassword(HashMap<String , Object> data) {
        UserInfo userInfo = userInfoRepository.getSingleEntityByFieldAndValue("user.login", data.get("email"));
        if (userInfo == null || !userInfo.getUser().isEnabled())
            return Result.Failed();

        String uuid = UUID.randomUUID().toString();

        try {
            Settings settings = settingsRepository.getSettings();
            String url = settings.getDomain() + "/#/"+userInfo.getLanguage().getName().toLowerCase()+"/restorePassword/" + uuid;
            emailService.sendTemplate(userInfo.getUser().getLogin(),settings.getEmail(), "Востановление пароля на getKnowledge();",
                    EmailTemplates.ForgotPassword,new String[] {settingsRepository.getSettings().getDomain(),url});
        } catch (Exception e) {
            trace.logException("Error send register email to " + userInfo.getUser().getLogin() , e , TraceLevel.Error,true);
            return Result.EmailNotSend();
        }

        SystemEvent systemEvent = systemEventRepository.createSystemEvent(userInfo,uuid,SystemEventType.RestorePassword);

        try {
            //next day
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE , 1);
            String jsonData = objectMapper.writeValueAsString(systemEvent);
            taskRepository.createTask("SystemEventService","removeRestorePasswordInfo",jsonData,calendar);
        } catch (JsonProcessingException e) {
            trace.logException("Can't parse restore password info to json" , e , TraceLevel.Warning,true);
        }

        return Result.Complete();
    }

    @Action(name = "getPosts" , mandatoryFields = {"userId","first","max"})
    @Transactional
    public List<PostMessage> getPosts(HashMap<String,Object> data){
        UserInfo selectedUser = userInfoRepository.read(longFromField("userId",data));
        if (selectedUser == null)
            return null;

        int first = (int) data.get("first");
        int max = (int) data.get("max");
        return userInfoRepository.postMessages(selectedUser,first,max);
    }

    @Action(name = "addPost" , mandatoryFields = {"userId","text"})
    @Transactional
    public Result addPost(HashMap<String,Object> data){
        UserInfo selectedUser = userInfoRepository.read(longFromField("userId",data));
        if (selectedUser == null)
            return Result.NotFound();

        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (currentUser == null)
            return  Result.NotAuthorized();


        String textMessage = (String) data.get("text");
        postMessageRepository.createMessage(currentUser,selectedUser,textMessage);

        return Result.Complete();
    }

    @Action(name = "addCommentToPost" , mandatoryFields = {"postId","text"})
    @Transactional
    public Result addCommentToPost(HashMap<String,Object> data){
        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (currentUser == null)
            return  Result.NotAuthorized();

        PostMessage basePost = postMessageRepository.read(longFromField("postId",data));
        if (basePost == null) {
            return Result.NotFound();
        }
        String textMessage = (String) data.get("text");
        postMessageRepository.createComment(currentUser,basePost,textMessage);

        return Result.Complete();
    }

    @Action(name = "removePost" , mandatoryFields = {"postId"})
    @Transactional
    public Result removePost(HashMap<String,Object> data) throws PlatformException {
        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null)
            return Result.NotAuthorized();

        Long postId = longFromField("postId",data);
        PostMessage postMessage = postMessageRepository.read(postId);
        if (postMessage == null) {
            return Result.NotFound();
        }

        if (postMessage.getSender().equals(userInfo) || postMessage.getRecipient().equals(userInfo)) {
            postMessageRepository.remove(postId);
        } else {
            return Result.NotAuthorized();
        }

        return Result.Complete();
    }

    @Action(name = "getDialogs")
    @Transactional
    public List<Dialog> getDialogs(HashMap<String,Object> data) {
        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (currentUser == null){
            return null;
        }

        return currentUser.getDialogs();
    }

    @Action(name = "addPrivacyMessage" , mandatoryFields = {"userId" , "text"})
    @Transactional
    public Result addPrivacyMessage(HashMap<String,Object> data){
        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (currentUser == null) {
            return Result.NotAuthorized();
        }
        Long userId = longFromField("userId",data);
        UserInfo companion = userInfoRepository.read(userId);
        if (companion == null){
            return Result.NotFound();
        }

        Dialog dialog = userInfoRepository.getDialog(currentUser,companion);
        Dialog dialog2 = userInfoRepository.getDialog(companion,currentUser);

        String text = (String) data.get("text");
        DialogMessage dialogMessage = dialogMessageRepository.createDialogMessage(currentUser,text,dialog,dialog2);
        dialog.getMessages().add(dialogMessage);
        dialog2.getMessages().add(dialogMessage);
        dialogRepository.merge(dialog);
        dialogRepository.merge(dialog2);
        Result result = Result.Complete();
        result.setObject(dialogMessage.getId());
        return result;
    }

    @Action(name = "removeDialog" , mandatoryFields = {"dialogId"})
    @Transactional
    public Result removeDialog(HashMap<String,Object> data){
        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (currentUser == null) {
            return Result.NotAuthorized();
        }

        Dialog dialog = dialogRepository.read(longFromField("dialogId",data));
        if (dialog == null){
            return Result.NotFound();
        }

        if (!dialog.getUser().equals(currentUser)){
            return Result.AccessDenied();
        }

        dialogRepository.remove(dialog);
        return Result.Complete();
    }

    @Action(name = "blockUser" , mandatoryFields = {"userId" , "blockMessage"})
    @Transactional
    public Result blockUser(HashMap<String,Object> data) {
        long userId = longFromField("userId",data);
        UserInfo userInfo = userInfoRepository.read(userId);
        if (userInfo == null) {
            return Result.NotFound("User not found");
        }

        if (!isAccessToEdit(data,userInfo)) {
            return Result.AccessDenied();
        }

        userRepository.blockUser(userInfo.getUser(), (String) data.get("blockMessage"));
        return Result.Complete();
    }

    @Action(name = "unblockUser" , mandatoryFields = {"userId"})
    @Transactional
    public Result unblockUser(HashMap<String,Object> data) {
        long userId = longFromField("userId",data);
        UserInfo userInfo = userInfoRepository.read(userId);
        if (userInfo == null) {
            return Result.NotFound("User not found");
        }

        if (!isAccessToEdit(data,userInfo)) {
            return Result.AccessDenied();
        }

        userRepository.unBlockUser(userInfo.getUser());
        return Result.Complete();
    }

    @Override
    @Transactional
    public byte[] getImageById(long id) {
        UserInfo userInfo = userInfoRepository.read(id);
        return userInfo == null ? null : userInfo.getProfileImage();
    }
}
