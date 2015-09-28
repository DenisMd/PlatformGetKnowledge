package com.getknowledge.platform.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.*;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.role.names.RoleName;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import com.getknowledge.platform.utils.ModuleLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/data")
public class DataController {
    @Autowired
    private ModuleLocator moduleLocator;

    @Autowired
    private UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(DataController.class);
    ObjectMapper objectMapper = new ObjectMapper();

//    ������ �� ������ ----------------------------------------------------------------------------

    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public @ResponseBody String read(@RequestParam(value = "id" ,required = true) Long id,
                                     @RequestParam(value ="className" , required = true) String className, Principal principal) throws PlatformException {
        try {
            if (id == null || className == null || className.isEmpty()) return null;
            Class classEntity = Class.forName(className);
            AbstractEntity entity = moduleLocator.findRepository(classEntity).read(id, classEntity);
            if (entity == null) {
                return null;
            }

            if (!isAccessRead(principal, entity)) {
                throw new NotAuthorized("access denied");
            }

            return objectMapper.writeValueAsString(entity);
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found");
        } catch (JsonProcessingException e) {
            logger.warn("can't parse entity " + className + " id " + id , e);
            throw new ParseException("can't parse entity " + className + " id " + id);
        }
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public @ResponseBody String count(@RequestParam(value = "className" , required = true) String className) throws PlatformException {
        try {
            if (className == null || className.isEmpty()) return null;
            Class classEntity = Class.forName(className);
            return moduleLocator.findRepository(classEntity).count(classEntity).toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found");
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public @ResponseBody String list(@RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            if (className == null || className.isEmpty()) return null;
            Class classEntity = Class.forName(className);
            List<AbstractEntity> list = moduleLocator.findRepository(classEntity).list(classEntity);

            if (list == null) {
                return "";
            }

            for (AbstractEntity abstractEntity : list) {
                if (!isAccessRead(principal, abstractEntity) ) {
                    throw new NotAuthorized("access denied");
                }
            }

            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            logger.warn("can't parse entities " + className, e);
            throw new ParseException("can't parse entities " + className);
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found");
        }
    }

    @RequestMapping(value = "/listPartial", method = RequestMethod.GET)
    public @ResponseBody String listPartial(@RequestParam("className") String className,
                       @RequestParam("first") Integer first, @RequestParam("max") Integer max, Principal principal) throws PlatformException {
        try {
            if (className == null || className.isEmpty() || first < 0 || max < 0) return null;
            Class classEntity = Class.forName(className);
            List<AbstractEntity> list = moduleLocator.findRepository(classEntity).listPartial(classEntity, first, max);

            if (list == null) {
                return "";
            }

            for (AbstractEntity abstractEntity : list) {
                if (!isAccessRead(principal, abstractEntity) ) {
                    throw new NotAuthorized("access denied");
                }
            }

            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            logger.warn("can't parse entities " + className, e);
            throw new ParseException("can't parse entities " + className);
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found");
        }
    }

//    ������ �� ��������� -----------------------------------------------------------------------------------

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional public @ResponseBody String create(@RequestParam("object") String jsonObject, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            if (jsonObject == null || className == null) return null;
            Class classEntity = Class.forName(className);
            AbstractEntity abstractEntity = (AbstractEntity) objectMapper.readValue(jsonObject, classEntity);
            if (!isAccessEdit(principal, abstractEntity) ) {
                throw new NotAuthorized("access denied");
            }
            moduleLocator.findRepository(classEntity).create(abstractEntity);
            return "object created";
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found");
        } catch (IOException e) {
            logger.warn("can't parse entities " + className, e);
            throw new ParseException("can't parse entities " + className);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody String update(@RequestParam("object") String jsonObject, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            if (jsonObject == null || className == null) return null;
            Class classEntity = Class.forName(className);
            AbstractEntity abstractEntity = (AbstractEntity) objectMapper.readValue(jsonObject, classEntity);
            if (isAccessEdit(principal, abstractEntity) ) {
                throw new NotAuthorized("access denied");
            }
            moduleLocator.findRepository(classEntity).update(abstractEntity);
            return "object updated";
        } catch (IOException e) {
            logger.warn("can't parse entities " + className, e);
            throw new ParseException("can't parse entities " + className);
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found");
        }
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    @Transactional public @ResponseBody String remove(@RequestParam("id") Long id, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            if (id == null || className == null) return null;
            Class classEntity = Class.forName(className);

            AbstractEntity abstractEntity = moduleLocator.findRepository(classEntity).read(id, classEntity);
            if (!isAccessEdit(principal, abstractEntity) ) {
                throw new NotAuthorized("access denied");
            }
            moduleLocator.findRepository(classEntity).remove(id, classEntity);

            return "object removed";
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found");
        }
    }

    @RequestMapping(value = "/action", method = RequestMethod.POST)
    @Transactional public @ResponseBody
    String action(@RequestParam("className") String className, @RequestParam("actionName") String actionName, @RequestParam("data") String jsonData , Principal principal) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            AbstractService abstractService = moduleLocator.findService(classEntity);

            for (Method method : abstractService.getClass().getMethods()) {
                Action action = AnnotationUtils.findAnnotation(method, Action.class);
                if(action == null) {
                    continue;
                }

                if (action.name().equals(actionName)) {
                    TypeReference<HashMap<String, Object>> typeRef
                            = new TypeReference<HashMap<String, Object>>() {
                    };

                    HashMap<String, Object> data = objectMapper.readValue(jsonData, typeRef);

                    if (!action.mandatoryFields()[0].isEmpty()) {
                        for (String mandatoryField : action.mandatoryFields()) {
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
                    Object result = method.invoke(abstractService, data);
                    return objectMapper.writeValueAsString(result);
                }
            }

            return null;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound("classname : " + className + " not found");
        } catch (IOException e) {
            logger.warn("parse result exception ", e);
            throw new ParseException("parse result exception");
        } catch (InvocationTargetException e) {
            logger.warn("InvocationTargetException ", e);
            throw new InvokeException("InvocationTargetException");
        } catch (IllegalAccessException e) {
            logger.warn("IllegalAccessException ", e);
            throw new InvokeException("IllegalAccessException");
        }
    }

    // �������� ���� ������� -----------------------------------------------------------

    private boolean isAccessRead(Principal principal, AbstractEntity abstractEntity) throws NotAuthorized {
        AuthorizationList al = abstractEntity.getAuthorizationList();
        if (al != null && al.getPermissionsForRead().isEmpty()) {
            return true;
        }

        if (principal == null) {
            return false;
        }

        User user = userRepository.getSingleEntityByFieldAndValue(User.class , "login",principal.getName());
        if (user == null) throw new NotAuthorized("User not found");;

        if (user.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            return true;
        }


        if (al == null) {return false;}

        return checkRight(user, al.getPermissionsForRead()) || checkUserList(user , al.getUserList());
    }

    private boolean isAccessEdit(Principal principal, AbstractEntity abstractEntity) throws NotAuthorized {
        AuthorizationList al = abstractEntity.getAuthorizationList();
        if (al != null && al.allowCreateEveryOne) return true;

        if (principal == null) {
            return false;
        }

        User user = userRepository.getSingleEntityByFieldAndValue(User.class , "login",principal.getName());
        if (user == null) throw new NotAuthorized("User not found");;

        if (user.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            return true;
        }

        if (al == null) {return false;}

        return checkRight(user, al.getPermissionsForEdit()) || checkUserList(user , al.getUserList());
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
        if (users == null) return false;
        return users.contains(user);
    }
}