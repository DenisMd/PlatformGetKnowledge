package com.getknowledge.platform.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.PrepareEntity;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.FileLinkService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.exceptions.*;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.role.names.RoleName;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import com.getknowledge.platform.utils.ModuleLocator;
import com.getknowledge.platform.utils.MultipartFileSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/data")
public class DataController {

    @Autowired
    ServletContext servletContext;

    @Autowired
    private ModuleLocator moduleLocator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraceService trace;

    static ObjectMapper objectMapper = new ObjectMapper();
    static {
        Hibernate4Module hbm = new Hibernate4Module();
        hbm.disable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION);
        hbm.enable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
        objectMapper.registerModule(hbm);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

//    Methods for read ----------------------------------------------------------------------------

    private AbstractEntity prepare (AbstractEntity entity , BaseRepository<AbstractEntity> repository,Principal principal, List<String> classNames) {
        if (repository instanceof PrepareEntity) {

            if (repository instanceof ProtectedRepository) {
                ProtectedRepository protectedRepository = (ProtectedRepository) repository;
                protectedRepository.setCurrentUser(getCurrentUser(principal));
            }

            try {
                properties : for (PropertyDescriptor pd : Introspector.getBeanInfo(entity.getClass()).getPropertyDescriptors()) {
                    if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                        Object result = pd.getReadMethod().invoke(entity);
                        if (result == null) continue;
                        if (result instanceof AbstractEntity) {

                            for (Annotation annotation : pd.getReadMethod().getAnnotations()) {
                                if (annotation instanceof JsonIgnore) {
                                    continue properties;
                                }
                            }

                            AbstractEntity abstractEntity = (AbstractEntity) result;
                            BaseRepository<AbstractEntity> repository2 = (BaseRepository<AbstractEntity>)moduleLocator.findRepository(abstractEntity.getClass());
                            pd.getWriteMethod().invoke(entity, prepare(abstractEntity,repository2,principal,classNames));
                        }
                    }
                }
            } catch (IntrospectionException | InvocationTargetException | IllegalAccessException | ModuleNotFound   e) {
                trace.logException("prepare exception", e, TraceLevel.Error);
            }

            return ((PrepareEntity) repository).prepare(entity);
        }
        return entity;
    }

    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public @ResponseBody String read(@RequestParam(value = "id" ,required = true) Long id,
                                     @RequestParam(value ="className" , required = true) String className, Principal principal) throws PlatformException {
        try {
            if (id == null || className == null || className.isEmpty()) return null;
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);
            AbstractEntity entity  = repository.read(id);

            if (entity == null) {
                return null;
            }

            if (!isAccessRead(principal, entity)) {
                throw new NotAuthorized("access denied for read entity: " + className, trace , TraceLevel.Warning);
            }

            entity = prepare(entity,repository,principal,new ArrayList<>());

            ObjectNode objectNode = objectMapper.valueToTree(entity);
            objectNode.put("editable" , isAccessEdit(principal,entity));
            objectNode.put("creatable" , isAccessCreate(principal, entity));
            return objectNode.toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found", trace , TraceLevel.Warning);
        }
    }

    @RequestMapping(value = "/readVideo", method = RequestMethod.GET)
    public void readVideoFile(@RequestParam(value = "id" ,required = true) Long id,
                              @RequestParam(value ="className" , required = true) String className, Principal principal, HttpServletRequest request, HttpServletResponse response) throws PlatformException {
        try {
            if (id == null || className == null || className.isEmpty()) return;
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);
            if (repository instanceof ProtectedRepository) {
                ProtectedRepository<?> protectedRepository = (ProtectedRepository<?>) repository;
                protectedRepository.setCurrentUser(getCurrentUser(principal));
            }
            AbstractEntity entity = repository.read(id);
            if (entity == null) {
                return;
            }

            if (!isAccessRead(principal, entity)) {
                throw new NotAuthorized("access denied for read video" , trace, TraceLevel.Warning);
            }

            AbstractService abstractService = moduleLocator.findService(classEntity);
            if (abstractService instanceof FileLinkService) {
                FileLinkService fileLinkService = (FileLinkService) abstractService;

                String videoUrl = servletContext.getRealPath(fileLinkService.getFileLink(id));
                MultipartFileSender.fromPath(Paths.get(videoUrl))
                        .with(request)
                        .with(response)
                        .serveResource();
            }


        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found", trace , TraceLevel.Warning);
        } catch (Exception e) {
            trace.logException("read video exception: " + e.getMessage(), e, TraceLevel.Warning);
        }
    }

    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "id" ,required = true) Long id,
                              @RequestParam(value ="className" , required = true) String className, Principal principal, HttpServletRequest request, HttpServletResponse response) throws PlatformException {
        try {
            if (id == null || className == null || className.isEmpty()) return null;
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);
            if (repository instanceof ProtectedRepository) {
                ProtectedRepository<?> protectedRepository = (ProtectedRepository<?>) repository;
                protectedRepository.setCurrentUser(getCurrentUser(principal));
            }
            AbstractEntity entity = repository.read(id);
            if (entity == null) {
                return null;
            }

            if (!isAccessRead(principal, entity)) {
                throw new NotAuthorized("access denied for read image" , trace, TraceLevel.Warning);
            }
            AbstractService abstractService = moduleLocator.findService(classEntity);
            if (abstractService instanceof ImageService) {
                ImageService imageService = ((ImageService)abstractService);
                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                return new ResponseEntity<>(imageService.getImageById(id), headers, HttpStatus.OK);
            }


        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found", trace , TraceLevel.Warning);
        } catch (Exception e) {
            trace.logException("read image exception: " + e.getMessage(), e, TraceLevel.Warning);
        }
        return null;
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public @ResponseBody String count(@RequestParam(value = "className" , required = true) String className) throws PlatformException {
        try {
            if (className == null || className.isEmpty()) return null;
            Class classEntity = Class.forName(className);
            return moduleLocator.findRepository(classEntity).count().toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found", trace , TraceLevel.Warning);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public @ResponseBody String list(@RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            if (className == null || className.isEmpty()) return null;
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);
            if (repository instanceof ProtectedRepository) {
                ProtectedRepository<?> protectedRepository = (ProtectedRepository<?>) repository;
                protectedRepository.setCurrentUser(getCurrentUser(principal));
            }
            List<AbstractEntity> list = repository.list();

            if (list == null) {
                return "";
            }
            String jsonResult = "[";
            for (AbstractEntity abstractEntity : list) {
                if (!isAccessRead(principal, abstractEntity) ) {
                    // TODO: question may continue?
                    throw new NotAuthorized("access denied for read entity from list" , trace, TraceLevel.Warning);
                }
                prepare(abstractEntity,repository,principal,new ArrayList<>());
                ObjectNode objectNode = objectMapper.valueToTree(abstractEntity);
                objectNode.put("editable" , isAccessEdit(principal,abstractEntity));
                objectNode.put("creatable" , isAccessCreate(principal,abstractEntity));
                jsonResult += objectNode.toString();
                jsonResult += ",";
            }
            if (jsonResult.length() > 1)
                jsonResult = jsonResult.substring(0,jsonResult.length()-1);

            jsonResult += "]";

            return jsonResult;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found", trace , TraceLevel.Warning);
        }
    }

    @RequestMapping(value = "/listPartial", method = RequestMethod.GET)
    public @ResponseBody String listPartial(@RequestParam("className") String className,
                       @RequestParam("first") Integer first, @RequestParam("max") Integer max, Principal principal) throws PlatformException {
        try {
            if (className == null || className.isEmpty() || first < 0 || max < 0) return null;
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);
            if (repository instanceof ProtectedRepository) {
                ProtectedRepository<?> protectedRepository = (ProtectedRepository<?>) repository;
                protectedRepository.setCurrentUser(getCurrentUser(principal));
            }
            List<AbstractEntity> list = repository.listPartial(first, max);

            if (list == null) {
                return "";
            }

            String jsonResult = "[";
            for (AbstractEntity abstractEntity : list) {
                if (!isAccessRead(principal, abstractEntity) ) {
                    // TODO: question may continue?
                    throw new NotAuthorized("access denied for read entity from list partial" , trace, TraceLevel.Warning);
                }
                prepare(abstractEntity,repository,principal,new ArrayList<>());
                ObjectNode objectNode = objectMapper.valueToTree(abstractEntity);
                objectNode.put("editable" , isAccessEdit(principal,abstractEntity));
                objectNode.put("creatable" , isAccessCreate(principal,abstractEntity));
                jsonResult += objectNode.toString();
                jsonResult += ",";
            }
            if (jsonResult.length() > 1)
                jsonResult = jsonResult.substring(0,jsonResult.length()-1);

            jsonResult += "]";

            return jsonResult;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found", trace , TraceLevel.Warning);
        }
    }

//    methods for change -----------------------------------------------------------------------------------

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestParam("object") String jsonObject, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            if (jsonObject == null || className == null) return null;
            Class classEntity = Class.forName(className);
            AbstractEntity abstractEntity = (AbstractEntity) objectMapper.readValue(jsonObject, classEntity);
            if (!isAccessCreate(principal, abstractEntity) ) {
                throw new NotAuthorized("access denied for create entity" , trace, TraceLevel.Warning);
            }
            moduleLocator.findRepository(classEntity).create(abstractEntity);
            return "object created";
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found" , trace , TraceLevel.Warning);
        } catch (IOException e) {
            throw new ParseException("can't parse entities for create " + className , trace, TraceLevel.Warning, e);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestParam("object") String jsonObject, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            if (jsonObject == null || className == null) return null;
            Class classEntity = Class.forName(className);
            AbstractEntity abstractEntity = (AbstractEntity) objectMapper.readValue(jsonObject, classEntity);
            if (!isAccessEdit(principal, abstractEntity) ) {
                throw new NotAuthorized("access denied for update entity" , trace, TraceLevel.Warning);
            }
            moduleLocator.findRepository(classEntity).update(abstractEntity);
            return "object updated";
        } catch (IOException e) {
            throw new ParseException("can't parse entities for update " + className,trace,TraceLevel.Warning,e);
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found", trace , TraceLevel.Warning);
        }
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public @ResponseBody String remove(@RequestParam("id") Long id, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            if (id == null || className == null) return null;
            Class classEntity = Class.forName(className);

            AbstractEntity abstractEntity = moduleLocator.findRepository(classEntity).read(id);
            if (!isAccessRemove(principal, abstractEntity) ) {
                throw new NotAuthorized("access denied for remove entity" , trace, TraceLevel.Warning);
            }
            moduleLocator.findRepository(classEntity).remove(id);

            return "object removed";
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found", trace , TraceLevel.Warning);
        }
    }

    private HashMap<String, Object> getDataForAction(String name, String actionName , String [] mandatoryFields, String jsonData, Principal principal) throws MandatoryFieldNotContainException, IOException, InvocationTargetException, IllegalAccessException {
        if (name.equals(actionName)) {
            TypeReference<HashMap<String, Object>> typeRef
                    = new TypeReference<HashMap<String, Object>>() {
            };

            HashMap<String, Object> data = objectMapper.readValue(jsonData, typeRef);

            if (!mandatoryFields[0].isEmpty()) {
                for (String mandatoryField : mandatoryFields) {
                    if (!data.containsKey(mandatoryField)) {
                        throw new MandatoryFieldNotContainException("mandatory field not contain " + mandatoryField);
                    }
                }
            }

            if(principal != null) {
                data.put("principalName", principal.getName());
            } else {
                data.put("principalName" , null);
            }
            return data;
        }
        return null;
    }

    @RequestMapping(value = "/action", method = RequestMethod.POST)
    public @ResponseBody
    String action(@RequestParam("className") String className, @RequestParam("actionName") String actionName, @RequestParam("data") String jsonData
            ,Principal principal) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            AbstractService abstractService = moduleLocator.findService(classEntity);

            for (Method method : abstractService.getClass().getMethods()) {
                Action action = AnnotationUtils.findAnnotation(method, Action.class);
                if(action == null) {
                    continue;
                }
                HashMap<String,Object> data = getDataForAction(actionName, action.name() , action.mandatoryFields(), jsonData,principal);
                if (data != null) {
                    Object result = method.invoke(abstractService, data);
                    return objectMapper.writeValueAsString(result);
                }
            }

            return null;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found", trace , TraceLevel.Warning);
        } catch (IOException e) {
            throw new ParseException("can't parse result for action " + className,trace,TraceLevel.Warning,e);
        } catch (InvocationTargetException e) {
            trace.logException("InvocationTargetException", e, TraceLevel.Warning);
            throw new InvokeException("InvocationTargetException");
        } catch (IllegalAccessException e) {
            trace.logException("IllegalAccessException", e, TraceLevel.Warning);
            throw new InvokeException("IllegalAccessException");
        }
    }

    @RequestMapping(value = "/actionWithFile", method = RequestMethod.POST, headers=("content-type=multipart/*"))
    public @ResponseBody String actionWithFile(@RequestParam("className") String className, @RequestParam("actionName") String actionName,
                                               @RequestParam("data") String jsonData, @RequestParam("file") MultipartFile file,
                                               Principal principal) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            AbstractService abstractService = moduleLocator.findService(classEntity);

            for (Method method : abstractService.getClass().getMethods()) {
                ActionWithFile action = AnnotationUtils.findAnnotation(method, ActionWithFile.class);
                if(action == null) {
                    continue;
                }

                HashMap<String,Object> data = getDataForAction(actionName, action.name(), action.mandatoryFields(), jsonData, principal);

                if (data != null) {
                    Object result = method.invoke(abstractService, data, file);
                    return objectMapper.writeValueAsString(result);
                }
            }

            return null;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found", trace , TraceLevel.Warning);
        } catch (IOException e) {
            trace.logException("parse result exception ", e, TraceLevel.Warning);
            throw new ParseException("parse result exception");
        } catch (InvocationTargetException e) {
            throw new InvokeException("InvocationTargetException", trace , TraceLevel.Warning , e);
        } catch (IllegalAccessException e) {
            throw new InvokeException("IllegalAccessException", trace , TraceLevel.Warning , e);
        }
    }


    // Authorization -----------------------------------------------------------

    private User getCurrentUser(Principal p) {
        return p == null ? null : userRepository.getSingleEntityByFieldAndValue("login",p.getName());
    }

    private boolean isAccessRead(Principal principal, AbstractEntity abstractEntity) throws NotAuthorized {
        AuthorizationList al = abstractEntity.getAuthorizationList();
        if (al != null && al.allowReadEveryOne) {
            return true;
        }

        if (principal == null) {
            return false;
        }

        User user = getCurrentUser(principal);
        if (user == null) throw new NotAuthorized("User not found");

        if (user.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            return true;
        }


        return al != null && (checkRight(user, al.getPermissionsForRead()) || checkUserList(user, al.getUserList()));

    }

    private boolean isAccessCreate(Principal principal, AbstractEntity abstractEntity) throws NotAuthorized {
        AuthorizationList al = abstractEntity.getAuthorizationList();
        if (al != null && al.allowCreateEveryOne) return true;

        if (principal == null) {
            return false;
        }

        User user = getCurrentUser(principal);
        if (user == null) return false;

        if (user.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            return true;
        }

        return al != null && (checkRight(user, al.getPermissionsForCreate()) || checkUserList(user, al.getUserList()));

    }

    private boolean isAccessEdit(Principal principal, AbstractEntity abstractEntity) throws NotAuthorized {
        AuthorizationList al = abstractEntity.getAuthorizationList();

        if (principal == null) {
            return false;
        }

        User user = getCurrentUser(principal);
        if (user == null) return false;

        if (user.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            return true;
        }

        return al != null && (checkRight(user, al.getPermissionsForEdit()) || checkUserList(user, al.getUserList()));

    }

    private boolean isAccessRemove(Principal principal, AbstractEntity abstractEntity) throws NotAuthorized {
        AuthorizationList al = abstractEntity.getAuthorizationList();

        if (principal == null) {
            return false;
        }

        User user = getCurrentUser(principal);
        if (user == null) return false;

        if (user.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            return true;
        }

        return al != null && (checkRight(user, al.getPermissionsForRemove()) || checkUserList(user, al.getUserList()));

    }

    private boolean checkRight(User user , List<Permission> permissions) throws NotAuthorized {

        if (user == null || permissions == null || permissions.isEmpty()) return false;

        for (Permission element : user.getPermissions()) {
            if (permissions.contains(element)) return true;
        }

        for (Permission element : user.getRole().getPermissions()) {
            if (permissions.contains(element)) return true;
        }

        return false;
    }

    private boolean checkUserList(User user, List<User> users) {
        return users != null && users.contains(user);
    }
}