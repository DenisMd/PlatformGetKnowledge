package com.getknowledge.platform.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
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
import com.getknowledge.platform.modules.role.names.RoleName;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
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
        ObjectNode objectNode = objectMapper.valueToTree(abstractEntity);
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

    private ArrayNode listToJsonString(List<AbstractEntity> list,User user,BaseRepository repository,Class<?> classEntity) throws Exception {
        ArrayNode nodes = objectMapper.createArrayNode();
        for (AbstractEntity abstractEntity : list) {
            if (!isAccessRead(user, abstractEntity) ) {
                if (abstractEntity.isContinueIfNotEnoughRights()) {
                    continue;
                }
                throw new NotAuthorized("Access denied for read entity from list" , trace, TraceLevel.Warning);
            }

            boolean isEditable = isAccessEdit(user,abstractEntity);
            boolean isCreatable = isAccessCreate(user,abstractEntity);
            abstractEntity = crudService.prepare(abstractEntity,repository,user);
            nodes.add(prepareJson(abstractEntity,isEditable,isCreatable,classEntity));
        }

        return nodes;
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

    //Авторизационные проверки -----------------------------------------------------------

    private User getCurrentUser(Principal p) {
        return p == null ? null : userRepository.getSingleEntityByFieldAndValue("login",p.getName());
    }

    private boolean isAccessRead(User user, AbstractEntity abstractEntity) throws NotAuthorized {
        AuthorizationList al = abstractEntity.getAuthorizationList();
        if (al != null && al.allowReadEveryOne) {
            return true;
        }

        if (user == null) throw new NotAuthorized("User not found");

        if (user.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            return true;
        }


        return al != null && al.isAccessRead(user);

    }

    private boolean isAccessCreate(User user, AbstractEntity abstractEntity) throws NotAuthorized {
        AuthorizationList al = abstractEntity.getAuthorizationList();
        if (al != null && al.allowCreateEveryOne) return true;

        if (user == null) return false;

        if (user.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            return true;
        }

        return al != null && al.isAccessCreate(user);

    }

    private boolean isAccessEdit(User user, AbstractEntity abstractEntity) throws NotAuthorized {
        AuthorizationList al = abstractEntity.getAuthorizationList();
        if (user == null) return false;

        if (user.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            return true;
        }

        return al != null && al.isAccessEdit(user);

    }

    private boolean isAccessRemove(User user, AbstractEntity abstractEntity) throws NotAuthorized {
        AuthorizationList al = abstractEntity.getAuthorizationList();
        if (user == null) return false;

        if (user.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            return true;
        }

        return al != null && al.isAccessRemove(user);

    }

    private boolean checkUserList(User user, List<User> users) {
        return users != null && users.contains(user);
    }


    //Методы для четния ----------------------------------------------------------------------------
    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public @ResponseBody String read(@RequestParam(value = "id" ,required = true) Long id,
                                     @RequestParam(value ="className" , required = true) String className, Principal principal) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);

            User user = getCurrentUser(principal);
            AbstractEntity entity  = crudService.read(repository,id);

            if (entity == null) {
                throw new NotFound(String.format("Entity (%s) by id : %d not found",className,id));
            }

            if (!isAccessRead(user, entity)) {
                throw new NotAuthorized(String.format("Access denied for read entity (%s) by id : %d",className,id), trace , TraceLevel.Warning);
            }

            boolean isEditable = isAccessEdit(user,entity);
            boolean isCreatable = isAccessCreate(user,entity);
            entity = crudService.prepare(entity,repository,user);

            return prepareJson(entity,isEditable,isCreatable,classEntity).toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,TraceLevel.Error,e);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public @ResponseBody String list(@RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);

            if (repository.count() > repository.getMaxCountsEntities()) {
                throw new EntityLimitException("Violated limit entity " + repository.getMaxCountsEntities());
            }

            List<AbstractEntity> list = crudService.list(repository);

            if (list == null) {
                return null;
            }

            return listToJsonString(list,getCurrentUser(principal),repository,classEntity).toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,TraceLevel.Error,e);
        }
    }

    @RequestMapping(value = "/listPartial", method = RequestMethod.GET)
    public @ResponseBody String listPartial(@RequestParam("className") String className,
                                            @RequestParam("first") Integer first, @RequestParam("max") Integer max, Principal principal) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);

            if (max > repository.getMaxCountsEntities()) {
                throw new EntityLimitException("Violated limit entity " + repository.getMaxCountsEntities());
            }

            List<AbstractEntity> list = crudService.list(repository,first,max);

            if (list == null) {
                return null;
            }

            return listToJsonString(list,getCurrentUser(principal),repository,classEntity).toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,TraceLevel.Error,e);
        }
    }

    @RequestMapping(value = "/readVideo", method = RequestMethod.GET)
    public void readVideoFile(@RequestParam(value = "id" ,required = true) Long id,
                              @RequestParam(value ="className" , required = true) String className, Principal principal, HttpServletRequest request, HttpServletResponse response) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            AbstractService abstractService = moduleLocator.findService(classEntity);
            if (abstractService instanceof VideoLinkService) {
                VideoLinkService videoLinkService = (VideoLinkService) abstractService;

                if (moduleLocator.findRepository(classEntity).read(id) == null) {
                    throw new NotFound(String.format("Video not found by id %d",id));
                }

                if (!videoLinkService.isAccessToWatchVideo(id, getCurrentUser(principal))) {
                    throw new NotAuthorized("access denied for read video" , trace, TraceLevel.Warning);
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
        } catch (Exception e) {
            if (e.getCause() instanceof SocketException) {
                //Ничего не даелаем так пользователь просто выключил видео
            } else {
                throw new SystemError("Unhandled exception : " + e.getMessage(),trace,TraceLevel.Error,e);
            }

        }
    }

    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "id" ,required = true) Long id,
                              @RequestParam(value ="className" , required = true) String className, Principal principal, HttpServletRequest request, HttpServletResponse response) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);
            AbstractEntity entity = crudService.read(repository,id);
            if (entity == null) {
                return null;
            }

            User user = getCurrentUser(principal);
            if (!isAccessRead(user, entity)) {
                throw new NotAuthorized("Access denied for read image" , trace, TraceLevel.Warning);
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
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,TraceLevel.Error,e);
        }
    }

    @RequestMapping(value = "/readFile", method = RequestMethod.GET)
    public ResponseEntity<byte[]> readFile(@RequestParam(value = "id" ,required = true) Long id,
                                           @RequestParam(value ="className" , required = true) String className,@RequestParam(value = "key") String key, Principal principal, HttpServletRequest request, HttpServletResponse response) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);
            AbstractEntity entity = crudService.read(repository,id);
            if (entity == null) {
                return null;
            }

            User user = getCurrentUser(principal);

            if (!isAccessRead(user, entity)) {
                throw new NotAuthorized("Access denied for read image" , trace, TraceLevel.Warning);
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
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,TraceLevel.Error,e);
        }
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public @ResponseBody String count(@RequestParam(value = "className" , required = true) String className) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            return moduleLocator.findRepository(classEntity).count().toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        }
    }


    @RequestMapping(value = "/filter" , method = RequestMethod.POST)
    public @ResponseBody String filterMethod(@RequestParam("properties") String properties, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
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

//            searchText : {fields : [{fieldName1 : value1} , {fieldName2 : value2}] , or : true}
            if (data.containsKey("searchText")) {
                HashMap<String, Object> searchText = (HashMap<String, Object>) data.get("searchText");
                List<HashMap<String, Object>> fields = (List<HashMap<String, Object>>) searchText.get("fields");
                String [] fieldNames = new String[fields.size()];
                String [] fieldValues = new String[fields.size()];
                int i = 0;
                for (HashMap<String, Object> search : fields) {
                    for (String field : search.keySet()) {
                        fieldNames[i] = field;
                        fieldValues[i] = (String) search.get(field);
                    }
                    i++;
                }
                filterQuery.searchText(fieldNames,fieldValues, searchText.containsKey("or"));
                filterCountQuery.searchText(fieldNames,fieldValues, searchText.containsKey("or"));
            }

            //in : {fieldName : "name" , values : ["value1" , "value2"]}
            if (data.containsKey("in")) {
                HashMap<String , Object> in = (HashMap<String, Object>) data.get("in");
                String fieldName = (String) in.get("fieldName");
                List<String> list = (List<String>) in.get("values");
                filterQuery.in(fieldName , list);
                filterCountQuery.in(fieldName , list);
            }

            //equal : [{fieldName : "name" , value : "value"}]
            if (data.containsKey("equal")) {
                List<HashMap<String , Object>> equal = (List<HashMap<String, Object>>) data.get("equal");
                for (HashMap<String , Object> equalElement : equal) {
                    String fieldName = (String) equalElement.get("fieldName");
                    String value = (String) equalElement.get("value");
                    filterQuery.equal(fieldName, value);
                    filterCountQuery.equal(fieldName, value);
                }
            }

            int first = (int) data.get("first");
            int max   = (int) data.get("max");

            List<AbstractEntity> list = filterService.getList(filterQuery,first,max);

            if (list == null) {
                return null;
            }

            ObjectNode objectNode = objectMapper.createObjectNode();
            User user = getCurrentUser(principal);
            objectNode.put("totalEntitiesCount" , filterService.getCount(filterCountQuery));
            objectNode.putArray("list").addAll(listToJsonString(list,user,repository,classEntity));
            Constructor<?> cos = classEntity.getConstructor();
            AbstractEntity abstractEntity = (AbstractEntity) cos.newInstance();
            objectNode.put("creatable" , isAccessCreate(user, abstractEntity));

            return objectNode.toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,TraceLevel.Error,e);
        }
    }
    
    //Методы на изменение -----------------------------------------------------------------------------------
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestParam("object") String jsonObject, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            AbstractEntity abstractEntity = (AbstractEntity) objectMapper.readValue(jsonObject, classEntity);
            User user = getCurrentUser(principal);
            if (!isAccessCreate(user, abstractEntity) ) {
                throw new NotAuthorized("Access denied for create entity" , trace, TraceLevel.Warning);
            }
            crudService.create(moduleLocator.findRepository(classEntity),abstractEntity);
            return objectMapper.writeValueAsString("Create success");
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (IOException e) {
            throw new ParseException("Can't parse entities for create " + className , trace, TraceLevel.Warning, e);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestParam("object") String jsonObject, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            AbstractEntity abstractEntity = (AbstractEntity) objectMapper.readValue(jsonObject, classEntity);
            User user = getCurrentUser(principal);
            if (!isAccessEdit(user, abstractEntity) ) {
                throw new NotAuthorized("Access denied for update entity" , trace, TraceLevel.Warning);
            }
            crudService.update(moduleLocator.findRepository(classEntity),abstractEntity);
            return objectMapper.writeValueAsString("Update success");
        } catch (IOException e) {
            throw new ParseException("Can't parse entities for update " + className,trace,TraceLevel.Warning,e);
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        }
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public @ResponseBody String remove(@RequestParam("id") Long id, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);

            AbstractEntity abstractEntity = crudService.read(moduleLocator.findRepository(classEntity),id);
            if (!isAccessRemove(getCurrentUser(principal), abstractEntity) ) {
                throw new NotAuthorized("Access denied for remove entity" , trace, TraceLevel.Warning);
            }
            crudService.remove(moduleLocator.findRepository(classEntity),id);
            return objectMapper.writeValueAsString("Remove success");
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (JsonProcessingException e) {
            //Сюда мы не попдаем
            return null;
        }
    }

    @RequestMapping(value = "/action", method = RequestMethod.POST)
    public @ResponseBody
    String action(@RequestParam("className") String className, @RequestParam("actionName") String actionName, @RequestParam("data") String jsonData
            ,Principal principal) throws PlatformException {
        try {
            Class classEntity = Class.forName(className);
            AbstractService abstractService = moduleLocator.findService(classEntity);

            User currentUser = getCurrentUser(principal);

            for (Method method : abstractService.getClass().getMethods()) {
                Action action = AnnotationUtils.findAnnotation(method, Action.class);
                if(action == null) {
                    continue;
                }
                HashMap<String,Object> data = getDataForAction(actionName, action.name() , action.mandatoryFields(), jsonData,principal);
                if (data != null) {
                    Object result = method.invoke(abstractService, data);
                    if (action.prepareEntity()) {
                        if (result instanceof  AbstractEntity) {
                            AbstractEntity entity = (AbstractEntity) result;
                            boolean isEditable = isAccessEdit(currentUser,entity);
                            boolean isCreatable = isAccessCreate(currentUser,entity);

                            return prepareJson(entity,isEditable,isCreatable,classEntity).toString();
                        } else if(result instanceof List) {
                            return listToJsonString((List<AbstractEntity>) result,currentUser,moduleLocator.findRepository(classEntity),classEntity).toString();
                        }
                    }
                    //TODO: обработать result
                    if (result instanceof Result){
                    }
                    return objectMapper.writeValueAsString(result);
                }
            }

            trace.log("Action : " + actionName + " not found" , TraceLevel.Warning);
            return null;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (IOException e) {
            throw new ParseException("Can't parse result for action " + className,trace,TraceLevel.Warning,e);
        } catch (InvocationTargetException e) {
            trace.logException("InvocationTargetException", e, TraceLevel.Warning);
            throw new InvokeException("InvocationTargetException");
        } catch (IllegalAccessException e) {
            trace.logException("IllegalAccessException", e, TraceLevel.Warning);
            throw new InvokeException("IllegalAccessException");
        } catch (PlatformException pl){
          throw pl;
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,TraceLevel.Error,e);
        }
    }

    @RequestMapping(value = "/actionWithFile", method = RequestMethod.POST, headers=("content-type=multipart/*"))
    public @ResponseBody String actionWithFile(@RequestParam("className") String className, @RequestParam("actionName") String actionName,
                                               @RequestParam("data") String jsonData, @RequestParam("file") List<MultipartFile> files,
                                               Principal principal) throws PlatformException {
        try {

            if (files.size() > 20) {
                throw new LimitException("Files limit for one request is 20");
            }

            Class classEntity = Class.forName(className);
            AbstractService abstractService = moduleLocator.findService(classEntity);

            for (Method method : abstractService.getClass().getMethods()) {
                ActionWithFile action = AnnotationUtils.findAnnotation(method, ActionWithFile.class);
                if(action == null) {
                    continue;
                }

                HashMap<String,Object> data = getDataForAction(actionName, action.name(), action.mandatoryFields(), jsonData, principal);

                if (data != null) {
                    Object result = method.invoke(abstractService, data, files);
                    return objectMapper.writeValueAsString(result);
                }
            }

            trace.log("Action with file : " + actionName + " not found" , TraceLevel.Warning);
            return null;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (IOException e) {
            trace.logException("Parse result exception ", e, TraceLevel.Warning);
            throw new ParseException("Parse result exception");
        } catch (InvocationTargetException e) {
            throw new InvokeException("InvocationTargetException", trace , TraceLevel.Warning , e);
        } catch (IllegalAccessException e) {
            throw new InvokeException("IllegalAccessException", trace , TraceLevel.Warning , e);
        } catch (PlatformException pl) {
            throw pl;
        } catch (Exception e) {
            throw new SystemError("Unhandled exception : " + e.getMessage(),trace,TraceLevel.Error,e);
        }
    }

}