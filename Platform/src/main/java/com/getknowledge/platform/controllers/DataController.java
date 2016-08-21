package com.getknowledge.platform.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.annotations.Filter;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.FilterCountQuery;
import com.getknowledge.platform.base.repositories.FilterQuery;
import com.getknowledge.platform.base.repositories.enumerations.OrderRoute;
import com.getknowledge.platform.base.serializers.FileResponse;
import com.getknowledge.platform.base.services.*;
import com.getknowledge.platform.exceptions.*;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.role.names.BaseRoleName;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import com.getknowledge.platform.utils.ModuleLocator;
import com.getknowledge.platform.utils.MultipartFileSender;
import org.apache.catalina.connector.ClientAbortException;
import org.hibernate.JDBCException;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/data")
public class DataController {

    //Статическая инициализация -------------------------------------------------------
    static ObjectMapper objectMapper = new ObjectMapper();
    static {
        Hibernate4Module hbm = new Hibernate4Module();
        hbm.disable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION);
        hbm.enable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
        objectMapper.registerModule(hbm);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Autowired
    private CrudService crudService;

    @Autowired
    private ModuleLocator moduleLocator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraceService trace;

    @Autowired
    private FilterService filterService;

    //Методы для подготовки объектов -------------------------------------------------------------
    private ObjectNode prepareJson (AbstractEntity abstractEntity,boolean editable, boolean creatable, Class classEntity) throws NotAuthorized, ModuleNotFound {
        ObjectNode objectNode = crudService.convertToObjectNode(objectMapper,abstractEntity);
        objectNode.put("editable" , editable);
        objectNode.put("creatable" , creatable);
        objectNode.put("imageViewExist" , false);
        AbstractService abstractService = moduleLocator.findService(classEntity);
        if (abstractService instanceof ImageService) {
            ImageService imageService = (ImageService) abstractService;
            if (imageService.getImageById(abstractEntity.getId()) != null) {
                objectNode.put("imageViewExist" , true);
            }
        }
        return objectNode;
    }

    private ArrayNode listToJsonString(List<AbstractEntity> list,User user,BaseRepository repository,Class<?> classEntity,boolean checkAccess) throws Exception {
        ArrayNode nodes = objectMapper.createArrayNode();
        for (AbstractEntity abstractEntity : list) {
            if (checkAccess) {
                if (!isAccessRead(user, abstractEntity)) {
                    if (abstractEntity.isContinueIfNotEnoughRights()) {
                        continue;
                    }
                    throw new NotAuthorized(String.format("Access denied for read entity (%s) from list", classEntity.getName()), trace);
                }
            }

            boolean isEditable = isAccessEdit(user,abstractEntity);
            boolean isCreatable = isAccessCreate(user,abstractEntity);
            abstractEntity = crudService.prepare(abstractEntity,repository,user);
            nodes.add(prepareJson(abstractEntity,isEditable,isCreatable,classEntity));
        }

        return nodes;
    }

    private HashMap<String, Object> getDataForAction(String name, String actionName , String [] mandatoryFields, String jsonData, Principal principal, String ip) throws MandatoryFieldNotContainException, IOException, InvocationTargetException, IllegalAccessException {
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
            data.put("ipAddress",ip);
            return data;
        }
        return null;
    }

    private void processInvocationException(InvocationTargetException e) throws PlatformException {
        if (e.getTargetException() instanceof PlatformException) {
            throw (PlatformException) e.getTargetException();
        }
        if (e.getTargetException() instanceof javax.persistence.PersistenceException) {
            javax.persistence.PersistenceException persistence = (javax.persistence.PersistenceException) e.getTargetException();
            if (persistence.getCause() != null && persistence.getCause() instanceof JDBCException) {
                JDBCException jdbcException = (JDBCException) persistence.getCause();
                throw new PersistenceException(jdbcException.getSQLException().getMessage(),trace,TraceLevel.Error,persistence);
            }
        }
        throw new InvokeException("InvocationTargetException", trace, e);
    }

    //Авторизационные проверки -----------------------------------------------------------

    private User getCurrentUser(Principal p) {
        return p == null ? null : userRepository.getSingleEntityByFieldAndValue("login",p.getName());
    }

    private boolean isAccessRead(User user, AbstractEntity abstractEntity) {
        AuthorizationList al = abstractEntity.getAuthorizationList();
        if (al != null && al.allowReadEveryOne) {
            return true;
        }

        if (al != null && al.readFromAuthorizedService) {
            try {
                AbstractService abstractService = moduleLocator.findService(abstractEntity.getClass());
                if (abstractService instanceof AuthorizedService) {
                    AuthorizedService authorizedService = (AuthorizedService) abstractService;
                    return authorizedService.isAccessForRead(user,abstractEntity);
                }
            } catch (ModuleNotFound moduleNotFound) {
                trace.logException("Cannot find service for entity" , moduleNotFound,TraceLevel.Warning,false);
            }
        }

        if (user == null) return false;

        if (user.getRole().getRoleName().equals(BaseRoleName.ROLE_ADMIN())) {
            return true;
        }

        return al != null && (al.isAccessRead(user) || isAccessFromService(user,abstractEntity,1,al.allowUseAuthorizedService));

    }

    private boolean isAccessCreate(User user, AbstractEntity abstractEntity) {
        AuthorizationList al = abstractEntity.getAuthorizationList();
        if (al != null && al.allowCreateEveryOne) return true;

        if (al != null && al.createFromAuthorizedService) {
            try {
                AbstractService abstractService = moduleLocator.findService(abstractEntity.getClass());
                if (abstractService instanceof AuthorizedService) {
                    AuthorizedService authorizedService = (AuthorizedService) abstractService;
                    return authorizedService.isAccessForCreate(user,abstractEntity);
                }
            } catch (ModuleNotFound moduleNotFound) {
                trace.logException("Cannot find service for entity" , moduleNotFound,TraceLevel.Warning,false);
            }
        }

        if (user == null) return false;

        if (user.getRole().getRoleName().equals(BaseRoleName.ROLE_ADMIN())) {
            return true;
        }

        return al != null && (al.isAccessCreate(user) || isAccessFromService(user,abstractEntity,3,al.allowUseAuthorizedService));

    }

    private boolean isAccessEdit(User user, AbstractEntity abstractEntity) {
        AuthorizationList al = abstractEntity.getAuthorizationList();

        if (al != null && al.editFromAuthorizedService) {
            try {
                AbstractService abstractService = moduleLocator.findService(abstractEntity.getClass());
                if (abstractService instanceof AuthorizedService) {
                    AuthorizedService authorizedService = (AuthorizedService) abstractService;
                    return authorizedService.isAccessForEdit(user,abstractEntity);
                }
            } catch (ModuleNotFound moduleNotFound) {
                trace.logException("Cannot find service for entity" , moduleNotFound,TraceLevel.Warning,false);
            }
        }

        if (user == null) return false;

        if (user.getRole().getRoleName().equals(BaseRoleName.ROLE_ADMIN())) {
            return true;
        }

        return al != null && (al.isAccessEdit(user) || isAccessFromService(user,abstractEntity,2,al.allowUseAuthorizedService));

    }

    private boolean isAccessRemove(User user, AbstractEntity abstractEntity) {
        AuthorizationList al = abstractEntity.getAuthorizationList();

        if (al != null && al.removeFromAuthorizedService) {
            try {
                AbstractService abstractService = moduleLocator.findService(abstractEntity.getClass());
                if (abstractService instanceof AuthorizedService) {
                    AuthorizedService authorizedService = (AuthorizedService) abstractService;
                    return authorizedService.isAccessForRemove(user,abstractEntity);
                }
            } catch (ModuleNotFound moduleNotFound) {
                trace.logException("Cannot find service for entity" , moduleNotFound,TraceLevel.Warning,false);
            }
        }

        if (user == null) return false;

        if (user.getRole().getRoleName().equals(BaseRoleName.ROLE_ADMIN())) {
            return true;
        }

        return al != null && (al.isAccessRemove(user) || isAccessFromService(user,abstractEntity,4,al.allowUseAuthorizedService));

    }

    private boolean isAccessFromService(User currentUser,AbstractEntity abstractEntity,int type,boolean allow) {
        if (!allow) return allow;

        try {
            AbstractService abstractService = moduleLocator.findService(abstractEntity.getClass());
            if (abstractService instanceof AuthorizedService) {
                AuthorizedService authorizedService = (AuthorizedService) abstractService;
                switch (type) {
                    case 1: return authorizedService.isAccessForRead(currentUser,abstractEntity);
                    case 2: return authorizedService.isAccessForEdit(currentUser,abstractEntity);
                    case 3: return authorizedService.isAccessForCreate(currentUser,abstractEntity);
                    case 4: return authorizedService.isAccessForRemove(currentUser,abstractEntity);
                }
            }
            return false;
        } catch (ModuleNotFound moduleNotFound) {
            return false;
        }
    }

    private boolean checkUserList(User user, List<User> users) {
        return users != null && users.contains(user);
    }


    //Методы для четния ----------------------------------------------------------------------------
    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public @ResponseBody String read(@RequestParam(value = "id" ,required = true) Long id,
                                     @RequestParam(value ="className" , required = true) String className, Principal principal) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"READ\" request with parameters {id : %d , className : %s} from user \"%s\"",id,className,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);

            AbstractEntity entity  = crudService.read(repository,id);

            if (entity == null) {
                throw new NotFound(String.format("Entity (%s) by id : %d not found",className,id));
            }

            User user = getCurrentUser(principal);

            if (!isAccessRead(user, entity)) {
                throw new NotAuthorized(String.format("Access denied for read entity (%s) by id : %d",className,id), trace);
            }

            boolean isEditable = isAccessEdit(user,entity);
            boolean isCreatable = isAccessCreate(user,entity);
            entity = crudService.prepare(entity,repository,user);

            String resultJson = prepareJson(entity,isEditable,isCreatable,classEntity).toString();

            trace.log(String.format("<------ Send \"READ\" response with parameters {id : %d , className : %s} from user \"%s\" result : %s",id,className,principal==null?"guest":principal.getName(),resultJson),TraceLevel.Debug,false);

            return resultJson;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (PlatformException p) {
            throw p;
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,e);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public @ResponseBody String list(@RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"LIST\" request with parameters {className : %s} from user \"%s\"",className,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);

            if (repository.count() > repository.getMaxCountsEntities()) {
                throw new EntityLimitException(String.format("Violated limit entity %d for className : %s ",repository.getMaxCountsEntities(),className),trace);
            }

            List<AbstractEntity> list = crudService.list(repository);

            if (list == null) {
                trace.log(String.format("<------ Send \"LIST\" response with parameters {className : %s} from user \"%s\" result : %s",className,principal==null?"guest":principal.getName(),""),TraceLevel.Debug,false);
                return null;
            }

            String resultJson = listToJsonString(list,getCurrentUser(principal),repository,classEntity,true).toString();

            trace.log(String.format("<------ Send \"LIST\" response with parameters {className : %s} from user \"%s\" result : %s",className,principal==null?"guest":principal.getName(),resultJson),TraceLevel.Debug,false);

            return resultJson;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (PlatformException p) {
            throw p;
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,e);
        }
    }

    @RequestMapping(value = "/listPartial", method = RequestMethod.GET)
    public @ResponseBody String listPartial(@RequestParam("className") String className,
                                            @RequestParam("first") Integer first, @RequestParam("max") Integer max, Principal principal) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"LIST Partial\" request with parameters {className : %s , first : %d , max : %d} from user \"%s\"",className,first,max,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);

            if (max > repository.getMaxCountsEntities()) {
                throw new EntityLimitException(String.format("Violated limit entity %d for className : %s ",repository.getMaxCountsEntities(),className),trace);
            }

            List<AbstractEntity> list = crudService.list(repository,first,max);

            if (list == null) {
                return null;
            }

            String result = listToJsonString(list,getCurrentUser(principal),repository,classEntity,true).toString();

            trace.log(String.format("<------ Send \"LIST Partial\" response with parameters {className : %s , first : %d , max : %d} from user \"%s\" result : %s",className,first,max,principal==null?"guest":principal.getName(),result),TraceLevel.Debug,false);

            return result;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,e);
        }
    }

    @RequestMapping(value = "/readVideo", method = RequestMethod.GET)
    public void readVideoFile(@RequestParam(value = "id" ,required = true) Long id,
                              @RequestParam(value ="className" , required = true) String className, Principal principal, HttpServletRequest request, HttpServletResponse response) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"Read Video\" request with parameters {className : %s , id : %d} from user \"%s\"",className,id,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
            Class classEntity = Class.forName(className);
            AbstractService abstractService = moduleLocator.findService(classEntity);
            if (abstractService instanceof VideoLinkService) {
                VideoLinkService videoLinkService = (VideoLinkService) abstractService;

                AbstractEntity video = crudService.read(moduleLocator.findRepository(classEntity),id);
                if (video == null) {
                    throw new NotFound(String.format("Video not found by id %d",id));
                }

                if (!isAccessRead(getCurrentUser(principal), video)) {
                    throw new NotAuthorized("Access denied for read video" , trace);
                }

                String videoUrl = videoLinkService.getVideoLink(id);
                if(videoUrl != null) {
                    MultipartFileSender.fromPath(Paths.get(videoUrl))
                            .with(request)
                            .with(response)
                            .serveResource();
                }
            }


        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (PlatformException p) {
            throw p;
        } catch (Exception e) {
            if (e.getCause() instanceof SocketException || e instanceof ClientAbortException) {
                //Ничего не даелаем так пользователь просто выключил видео или выполнил перемотку
            } else {
                throw new SystemError("Unhandled exception : " + e.getMessage(),trace,e,true);
            }

        }
    }

    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "id" ,required = true) Long id,
                              @RequestParam(value ="className" , required = true) String className, Principal principal, HttpServletRequest request, HttpServletResponse response) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"Read Image\" request with parameters {className : %s , id : %d} from user \"%s\"",className,id,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);

            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);
            AbstractEntity entity = crudService.read(repository,id);
            if (entity == null) {
                return null;
            }

            User user = getCurrentUser(principal);
            if (!isAccessRead(user, entity)) {
                throw new NotAuthorized("Access denied for read image" , trace);
            }
            AbstractService abstractService = moduleLocator.findService(classEntity);
            if (abstractService instanceof ImageService) {
                ImageService imageService = ((ImageService)abstractService);
                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                return new ResponseEntity<>(imageService.getImageById(id), headers, HttpStatus.OK);
            }

            return null;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (PlatformException p) {
            throw p;
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,e);
        }
    }

    @RequestMapping(value = "/readFile", method = RequestMethod.GET)
    public ResponseEntity<byte[]> readFile(@RequestParam(value = "id" ,required = true) Long id,
                                           @RequestParam(value ="className" , required = true) String className,@RequestParam(value = "key") String key, Principal principal, HttpServletRequest request, HttpServletResponse response) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"Read File\" request with parameters {className : %s , id : %d} from user \"%s\"",className,id,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);

            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);
            AbstractEntity entity = crudService.read(repository,id);
            if (entity == null) {
                return null;
            }

            User user = getCurrentUser(principal);

            if (!isAccessRead(user, entity)) {
                throw new NotAuthorized("Access denied for read image" , trace);
            }
            AbstractService abstractService = moduleLocator.findService(classEntity);
            if (abstractService instanceof FileService) {
                FileService fileService = ((FileService)abstractService);
                FileResponse fileResponse = fileService.getFile(id,key);
                final HttpHeaders headers = new HttpHeaders();
                headers.add("content-disposition", "attachment; filename=" + fileResponse.getFileName());
                return new ResponseEntity<>(fileResponse.getData(), headers, HttpStatus.OK);
            }
            return null;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (PlatformException p) {
            throw p;
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,e);
        }
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public @ResponseBody String count(@RequestParam(value = "className" , required = true) String className) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"Count\" request with parameters {className : %s }",className),TraceLevel.Debug,false);
            Class classEntity = Class.forName(className);
            return moduleLocator.findRepository(classEntity).count().toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        }
    }


    //Формат для фильтров
    //{
    //  order : [{route : Asc , field : name1} , {route : Desc , field : name2}],
    //  distinct : true
    //  filtersInfo : {
    //      logicalExpression : "and/or",
    //      filters : [
    //          {
    //              name : "equals",
    //              field : "fieldName" -- user.role.roleName
    //              type : "" number/text/logical/date/enum
    //              values : ["aaa", "bbb"]
    //          }]
    //  },
    // customFilters : [{
    //      name : 'someName',
    //      data : {...}
    //  }
    //  }]
    // }
    @RequestMapping(value = "/filter" , method = RequestMethod.POST)
    public @ResponseBody String filterMethod(@RequestParam("properties") String properties, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"Filter\" request with parameters {className : %s , properties : %s} from user \"%s\"",className,properties,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);

            TypeReference<HashMap<String, Object>> typeRef
                    = new TypeReference<HashMap<String, Object>>() {
            };

            HashMap<String, Object> data = objectMapper.readValue(properties, typeRef);
            FilterQuery filterQuery = repository.initFilter();
            FilterCountQuery filterCountQuery = repository.initCountFilter();


//            order : [{route : Asc , field : name1} , {route : Desc , field : name2}]
            if (data.containsKey("order")) {
                List<HashMap<String, Object>> order = (List<HashMap<String, Object>>) data.get("order");
                for (HashMap<String, Object> orderField : order) {
                    OrderRoute orderRoute = OrderRoute.Asc;
                    if (orderField.containsKey("route")) {
                        orderRoute = OrderRoute.valueOf((String) orderField.get("route"));
                    }

                    if (orderField.containsKey("field")) {
                        filterQuery.setOrder((String) orderField.get("field"), orderRoute);
                    }
                }
            }

            if (data.containsKey("filtersInfo")) {
                HashMap<String, Object> filtersInfo = (HashMap<String, Object>) data.get("filtersInfo");

                // and/or
                String logicalExpression = (String) filtersInfo.get("logicalExpression");
                filterQuery.setLogicalExpression(logicalExpression);
                filterCountQuery.setLogicalExpression(logicalExpression);

                List<HashMap<String, Object>> filters = (List<HashMap<String, Object>>) filtersInfo.get("filters");

                for (HashMap<String, Object> filter : filters) {
                    List<String> values     = (List<String>) filter.get("values");
                    String fieldName        = (String) filter.get("field");
                    String type             = (String) filter.get("type");
                    String name             = (String)filter.get("name");

                    switch (name) {
                        case "equals" :
                            filterQuery.equal(fieldName,values.get(0),type);
                            filterCountQuery.equal(fieldName,values.get(0),type);
                            break;
                        case "like" :
                            filterQuery.like(fieldName, values.get(0));
                            filterCountQuery.like(fieldName, values.get(0));
                            break;
                        case "greatThan" :
                            filterQuery.greaterThan(fieldName,values.get(0),type);
                            filterCountQuery.greaterThan(fieldName,values.get(0),type);
                            break;
                        case "greaterThanOrEqualTo":
                            filterQuery.greaterThanOrEqualTo(fieldName, values.get(0), type);
                            filterCountQuery.greaterThanOrEqualTo(fieldName,values.get(0),type);
                            break;
                        case "lessThan":
                            filterQuery.lessThan(fieldName, values.get(0), type);
                            filterCountQuery.lessThan(fieldName, values.get(0), type);
                            break;
                        case "lessThanOrEqualTo":
                            filterQuery.lessThanOrEqualTo(fieldName, values.get(0), type);
                            filterCountQuery.lessThanOrEqualTo(fieldName, values.get(0), type);
                            break;
                        case "between":
                            filterQuery.between(fieldName, values, type);
                            filterCountQuery.between(fieldName, values, type);
                            break;
                        case "in":
                            filterQuery.in(fieldName,values,type);
                            filterCountQuery.in(fieldName,values,type);
                            break;
                        default: new ParseException("Error filter type : " + name, trace);
                    }
                }
            }

            if (data.containsKey("customFilters")) {
                List<HashMap<String, Object>> customFilters = (List<HashMap<String, Object>>) data.get("customFilters");
                for (HashMap<String,Object> customFilter : customFilters) {
                    String customFilterName = (String) customFilter.get("name");
                    HashMap<String,Object> customFilterData = (HashMap<String, Object>) customFilter.get("data");
                    if (principal != null) {
                        customFilterData.put("principalName", principal.getName());
                    } else {
                        customFilterData.put("principalName", "");
                    }
                    for (Method method : repository.getClass().getMethods()) {
                        Filter filter = AnnotationUtils.findAnnotation(method, Filter.class);
                        if(filter == null || !filter.name().equals(customFilterName)) {
                            continue;
                        }
                        method.invoke(repository, customFilterData , filterQuery,filterCountQuery);
                        break;
                    }
                }
            }

            if (data.containsKey("distinct")) {
                boolean distinct = (boolean)data.get("distinct");
                filterQuery.setDistinct(distinct);
                filterCountQuery.setDistinct(distinct);
            }

            int first = (int) data.get("first");
            int max   = (int) data.get("max");

            List<AbstractEntity> list = filterService.getList(filterQuery,first,max);
            ObjectNode objectNode = objectMapper.createObjectNode();
            User user = getCurrentUser(principal);
            objectNode.put("totalEntitiesCount" , filterService.getCount(filterCountQuery));
            if (list != null) {
                objectNode.putArray("list").addAll(listToJsonString(list, user, repository, classEntity,true));
            }
            Constructor<?> cos = classEntity.getConstructor();
            AbstractEntity abstractEntity = (AbstractEntity) cos.newInstance();
            objectNode.put("creatable" , isAccessCreate(user, abstractEntity));

            String jsonResult = objectNode.toString();
            trace.log(String.format("<------ Send \"Filter\" response with parameters {className : %s , properties : %s} result : %s from user \"%s\"",className,properties,jsonResult,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
            return jsonResult;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (InvocationTargetException e) {
            processInvocationException(e);
            //Сюда мы не попадем
            return "";
        } catch (PlatformException p) {
            throw p;
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,e);
        }
    }
    
    //Методы на изменение -----------------------------------------------------------------------------------
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody Result create(@RequestParam("object") String jsonObject, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"Create\" request with parameters {className : %s , object : %s} from user \"%s\"",className,jsonObject,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
            Class classEntity = Class.forName(className);
            AbstractEntity abstractEntity = (AbstractEntity) objectMapper.readValue(jsonObject, classEntity);
            User user = getCurrentUser(principal);
            if (!isAccessCreate(user, abstractEntity) ) {
                throw new NotAuthorized(String.format("Access denied for create entity (%s)",className) , trace);
            }
            crudService.create(moduleLocator.findRepository(classEntity),abstractEntity);
            return Result.Complete();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (IOException e) {
            throw new ParseException("Can't parse entities for create " + className , trace, e);
        }  catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,e);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody Result update(@RequestParam("object") String jsonObject, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"Update\" request with parameters {className : %s , object : %s} from user \"%s\"",className,jsonObject,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
            Class classEntity = Class.forName(className);
            AbstractEntity abstractEntity = (AbstractEntity) objectMapper.readValue(jsonObject, classEntity);
            User user = getCurrentUser(principal);
            if (!isAccessEdit(user, abstractEntity) ) {
                throw new NotAuthorized(String.format("Access denied for update entity (%s)",className) , trace);
            }
            crudService.update(moduleLocator.findRepository(classEntity),abstractEntity);
            return Result.Complete();
        } catch (IOException e) {
            throw new ParseException("Can't parse entities for update " + className,trace,e);
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,e);
        }
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public @ResponseBody Result remove(@RequestParam("id") Long id, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"Remove\" request with parameters {className : %s , id : %d} from user \"%s\"",className,id,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
            Class classEntity = Class.forName(className);

            AbstractEntity abstractEntity = crudService.read(moduleLocator.findRepository(classEntity),id);
            if (!isAccessRemove(getCurrentUser(principal), abstractEntity) ) {
                throw new NotAuthorized(String.format("Access denied for remove entity (%s)",className) , trace);
            }
            crudService.remove(moduleLocator.findRepository(classEntity),id);
            return Result.Complete();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,e);
        }
    }

    @RequestMapping(value = "/action", method = RequestMethod.POST)
    public @ResponseBody
    String action(@RequestParam("className") String className, @RequestParam("actionName") String actionName, @RequestParam("data") String jsonData
            ,Principal principal, HttpServletRequest request) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"Action\" request with parameters {className : %s , actionName : %s , data : %s} from user \"%s\"",className,actionName,jsonData,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
            String  jsonResult = null;
            Class classEntity = Class.forName(className);
            AbstractService abstractService = moduleLocator.findService(classEntity);

            User currentUser = getCurrentUser(principal);

            for (Method method : abstractService.getClass().getMethods()) {
                Action action = AnnotationUtils.findAnnotation(method, Action.class);
                if(action == null) {
                    continue;
                }
                HashMap<String,Object> data = getDataForAction(actionName, action.name() , action.mandatoryFields(), jsonData,principal, request.getRemoteAddr());
                if (data != null) {
                    Object result = method.invoke(abstractService, data);
                    if (action.prepareEntity()) {
                        BaseRepository baseRepository = null;
                        if (action.repositoryName().isEmpty()) {
                            baseRepository = moduleLocator.findRepository(classEntity);
                        } else {
                            baseRepository = moduleLocator.findRepository(action.repositoryName());
                        }
                        if (result instanceof  AbstractEntity) {
                            AbstractEntity entity = (AbstractEntity) result;
                            boolean isEditable = isAccessEdit(currentUser,entity);
                            boolean isCreatable = isAccessCreate(currentUser,entity);
                            entity = crudService.prepare(entity,baseRepository,getCurrentUser(principal));
                            jsonResult = prepareJson(entity,isEditable,isCreatable,classEntity).toString();
                            trace.log(String.format("<------ Send \"Action\" response with parameters {className : %s , actionName : %s , data : %s} result : %s from user \"%s\"",className,actionName,jsonData,jsonResult,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
                            return jsonResult;
                        } else if(result instanceof List) {
                            jsonResult = listToJsonString((List<AbstractEntity>) result,currentUser,baseRepository,classEntity,false).toString();
                            trace.log(String.format("<------ Send \"Action\" response with parameters {className : %s , actionName : %s , data : %s} result : %s from user \"%s\"",className,actionName,jsonData,jsonResult,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
                            return jsonResult;
                        }
                    }
                    //TODO: обработать result
                    if (result instanceof Result){
                    }
                    if (result instanceof AbstractEntity){
                        jsonResult = crudService.convertToObjectNode(objectMapper, (AbstractEntity) result).toString();
                    } else {
                        jsonResult = objectMapper.writeValueAsString(result);
                    }
                    trace.log(String.format("<------ Send \"Action\" response with parameters {className : %s , actionName : %s , data : %s} result : %s from user \"%s\"",className,actionName,jsonData,jsonResult,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
                    return jsonResult;
                }
            }

            return null;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (IOException e) {
            throw new ParseException("Can't parse result for action " + className,trace,e);
        } catch (InvocationTargetException e) {
            processInvocationException(e);
            //Сюда мы не попадем
            return "";
        } catch (IllegalAccessException e) {
            throw new InvokeException("IllegalAccessException", trace, e);
        } catch (PlatformException pl){
          throw pl;
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,e);
        }
    }

    @RequestMapping(value = "/actionWithFile", method = RequestMethod.POST, headers=("content-type=multipart/*"))
    public @ResponseBody String actionWithFile(@RequestParam("className") String className, @RequestParam("actionName") String actionName,
                                               @RequestParam("data") String jsonData, @RequestParam("file") List<MultipartFile> files,
                                               Principal principal, HttpServletRequest request) throws PlatformException {
        try {
            trace.log(String.format("------> Received \"Action with files\" request with parameters {className : %s , actionName : %s , data : %s} from user \"%s\"",className,actionName,jsonData,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
            if (files.size() > 20) {
                throw new LimitException("Files limit for one request is 20",trace);
            }

            Class classEntity = Class.forName(className);
            AbstractService abstractService = moduleLocator.findService(classEntity);

            for (Method method : abstractService.getClass().getMethods()) {
                ActionWithFile action = AnnotationUtils.findAnnotation(method, ActionWithFile.class);
                if(action == null) {
                    continue;
                }

                HashMap<String,Object> data = getDataForAction(actionName, action.name(), action.mandatoryFields(), jsonData, principal,request.getRemoteAddr());

                if (data != null) {
                    long maxSize = files.stream().mapToLong(f -> f.getSize()).sum();
                    if ((action.maxSize() * 1024) < maxSize) {
                        throw new MaxSizeException("Files is too big, max size for files is " + (action.maxSize() * 1024) + " bytes" , trace);
                    }
                    Object result = method.invoke(abstractService, data, files);
                    String jsonResult = objectMapper.writeValueAsString(result);
                    trace.log(String.format("<------ Send \"Action\" response with parameters {className : %s , actionName : %s , data : %s} result : %s from user \"%s\"",className,actionName,jsonData,jsonResult,principal==null?"guest":principal.getName()),TraceLevel.Debug,false);
                    return jsonResult;
                }
            }

            return null;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (IOException e) {
            trace.logException("Parse result exception ", e, TraceLevel.Warning,false);
            throw new ParseException("Parse result exception");
        } catch (InvocationTargetException e) {
            processInvocationException(e);
            //Сюда мы не попадем
            return "";
        } catch (IllegalAccessException e) {
            throw new InvokeException("IllegalAccessException", trace, e);
        } catch (PlatformException pl) {
            throw pl;
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,e);
        }
    }



}