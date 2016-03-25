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
import com.getknowledge.platform.base.repositories.FilterQuery;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.base.repositories.enumerations.OrderRoute;
import com.getknowledge.platform.base.serializers.FileResponse;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.VideoLinkService;
import com.getknowledge.platform.base.services.FileService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.exceptions.*;
import com.getknowledge.platform.modules.filter.FilterService;
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
    private ObjectNode prepareJson (AbstractEntity abstractEntity,boolean editable, boolean creatable, Class classEntity) throws NotAuthorized, ModuleNotFound {
        ObjectNode objectNode = objectMapper.valueToTree(abstractEntity);
        objectNode.put("editable" , editable);
        objectNode.put("creatable" , creatable);
        AbstractService abstractService = moduleLocator.findService(classEntity);
        if (abstractService instanceof ImageService) {
            ImageService imageService = (ImageService) abstractService;
            if (imageService.getImageById(abstractEntity.getId()) != null) {
                objectNode.put("imageViewExist" , true);
            }
        }
        return objectNode;
    }

    private ArrayNode listToJsonString(List<AbstractEntity> list,Principal principal,BaseRepository repository,Class<?> classEntity) throws Exception {
        ArrayNode nodes = objectMapper.createArrayNode();
        for (AbstractEntity abstractEntity : list) {
            if (!isAccessRead(principal, abstractEntity) ) {
                if (abstractEntity.isContinueIfNotEnoughRights()) {
                    continue;
                }
                throw new NotAuthorized("access denied for read entity from list" , trace, TraceLevel.Warning);
            }

            boolean isEditable = isAccessEdit(principal,abstractEntity);
            boolean isCreatable = isAccessCreate(principal,abstractEntity);
            abstractEntity = repository.prepare(abstractEntity,repository,getCurrentUser(principal),moduleLocator);
            nodes.add(prepareJson(abstractEntity,isEditable,isCreatable,classEntity));
        }

        return nodes;
    }

    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public @ResponseBody String read(@RequestParam(value = "id" ,required = true) Long id,
                                     @RequestParam(value ="className" , required = true) String className, Principal principal) throws PlatformException {
        try {
            if (id == null || className == null || className.isEmpty()) return null;
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);

            if (repository instanceof ProtectedRepository) {
                ProtectedRepository protectedRepository = (ProtectedRepository) repository;
                protectedRepository.setCurrentUser(getCurrentUser(principal));
            }

            AbstractEntity entity  = repository.read(id);

            if (entity == null) {
                return null;
            }

            if (!isAccessRead(principal, entity)) {
                throw new NotAuthorized("access denied for read entity: " + className, trace , TraceLevel.Warning);
            }

            boolean isEditable = isAccessEdit(principal,entity);
            boolean isCreatable = isAccessCreate(principal,entity);
            entity = repository.prepare(entity,repository,getCurrentUser(principal),moduleLocator);

            return prepareJson(entity,isEditable,isCreatable,classEntity).toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (Exception e) {
            trace.logException("Prepare exception : " + e.getMessage(),e, TraceLevel.Error);
            return null;
        }
    }



    @RequestMapping(value = "/readVideo", method = RequestMethod.GET)
    public void readVideoFile(@RequestParam(value = "id" ,required = true) Long id,
                              @RequestParam(value ="className" , required = true) String className, Principal principal, HttpServletRequest request, HttpServletResponse response) throws PlatformException {
        try {
            if (id == null || className == null || className.isEmpty()) return;
            Class classEntity = Class.forName(className);
            AbstractService abstractService = moduleLocator.findService(classEntity);
            if (abstractService instanceof VideoLinkService) {
                VideoLinkService videoLinkService = (VideoLinkService) abstractService;

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
            if (!(e.getCause() instanceof SocketException)) {
                //Ничего не даелаем так пользователь просто выключил видео
                trace.logException("read video exception: " + e.getMessage(), e, TraceLevel.Warning);
            }

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
            throw new ClassNameNotFound(className,trace);
        } catch (Exception e) {
            trace.logException("read image exception: " + e.getMessage(), e, TraceLevel.Warning);
        }
        return null;
    }

    @RequestMapping(value = "/readFile", method = RequestMethod.GET)
    public ResponseEntity<byte[]> readFile(@RequestParam(value = "id" ,required = true) Long id,
                                           @RequestParam(value ="className" , required = true) String className,@RequestParam(value = "key") String key, Principal principal, HttpServletRequest request, HttpServletResponse response) throws PlatformException {
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
            if (abstractService instanceof FileService) {
                FileService fileService = ((FileService)abstractService);
                FileResponse fileResponse = fileService.getFile(id,key);
                final HttpHeaders headers = new HttpHeaders();
                headers.add("content-disposition", "attachment; filename=" + fileResponse.getFileName());
                return new ResponseEntity<>(fileResponse.getData(), headers, HttpStatus.OK);
            }

        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
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
            throw new ClassNameNotFound(className,trace);
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

            if (repository.count() > repository.getMaxCountsEntities()) {
                throw new EntityLimitException("Violated limit entity " + repository.getMaxCountsEntities());
            }

            List<AbstractEntity> list = repository.list();

            if (list == null) {
                return "";
            }

            return listToJsonString(list,principal,repository,classEntity).toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (Exception e) {
            trace.logException("Prepare exception : " + e.getMessage(),e, TraceLevel.Error);
            return null;
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

            if (max > repository.getMaxCountsEntities()) {
                throw new EntityLimitException("Violated limit entity " + repository.getMaxCountsEntities());
            }

            List<AbstractEntity> list = repository.listPartial(first, max);

            if (list == null) {
                return "";
            }

            return listToJsonString(list,principal,repository,classEntity).toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (Exception e) {
            trace.logException("Prepare exception : " + e.getMessage(),e, TraceLevel.Error);
            return null;
        }
    }

    @Autowired
    private FilterService filterService;

    @RequestMapping(value = "/filter" , method = RequestMethod.POST)
    public @ResponseBody String filterMethod(@RequestParam("properties") String properties, @RequestParam("className") String className, Principal principal) throws PlatformException {
        try {
            if (className == null || className.isEmpty()) return null;
            Class classEntity = Class.forName(className);
            BaseRepository<AbstractEntity> repository = moduleLocator.findRepository(classEntity);
            if (repository instanceof ProtectedRepository) {
                ProtectedRepository<?> protectedRepository = (ProtectedRepository<?>) repository;
                protectedRepository.setCurrentUser(getCurrentUser(principal));
            }

            TypeReference<HashMap<String, Object>> typeRef
                    = new TypeReference<HashMap<String, Object>>() {
            };

            HashMap<String, Object> data = objectMapper.readValue(properties, typeRef);
            FilterQuery filterQuery = repository.initFilter();


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
            }

            //in : {fieldName : "name" , values : ["value1" , "value2"]}
            if (data.containsKey("in")) {
                HashMap<String , Object> in = (HashMap<String, Object>) data.get("in");
                String fieldName = (String) in.get("fieldName");
                List<String> list = (List<String>) in.get("values");
                filterQuery.in(fieldName , list);
            }

            //equal : [{fieldName : "name" , value : "value"}]
            if (data.containsKey("equal")) {
                List<HashMap<String , Object>> equal = (List<HashMap<String, Object>>) data.get("equal");
                for (HashMap<String , Object> equalElement : equal) {
                    String fieldName = (String) equalElement.get("fieldName");
                    String value = (String) equalElement.get("value");
                    filterQuery.equal(fieldName, value);
                }
            }

            int first = (int) data.get("first");
            int max   = (int) data.get("max");

            List<AbstractEntity> list = filterService.getList(filterQuery,first,max);

            if (list == null) {
                return "";
            }

            ObjectNode objectNode = objectMapper.createObjectNode();
           // objectNode.put("totalEntitiesCount" , );
            objectNode.putArray("list").addAll(listToJsonString(list,principal,repository,classEntity));
            Constructor<?> cos = classEntity.getConstructor();
            AbstractEntity abstractEntity = (AbstractEntity) cos.newInstance();
            objectNode.put("creatable" , isAccessCreate(principal, abstractEntity));

            return objectNode.toString();
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (Exception e) {
            trace.logException("Prepare exception : " + e.getMessage(),e, TraceLevel.Error);
            return null;
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
            return objectMapper.writeValueAsString("create success");
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
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
            return objectMapper.writeValueAsString("update success");
        } catch (IOException e) {
            throw new ParseException("can't parse entities for update " + className,trace,TraceLevel.Warning,e);
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
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

            return objectMapper.writeValueAsString("remove success");
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (JsonProcessingException e) {
            throw new ParseException("can't parse entities for remove " + className,trace,TraceLevel.Warning,e);
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
                    if (action.prepareEntity()) {
                        if (result instanceof  AbstractEntity) {
                            AbstractEntity entity = (AbstractEntity) result;
                            boolean isEditable = isAccessEdit(principal,entity);
                            boolean isCreatable = isAccessCreate(principal,entity);

                            return prepareJson(entity,isEditable,isCreatable,classEntity).toString();
                        } else if(result instanceof List) {
                            return listToJsonString((List<AbstractEntity>) result,principal,moduleLocator.findRepository(classEntity),classEntity).toString();
                        }
                    }
                    return objectMapper.writeValueAsString(result);
                }
            }

            trace.log("Action : " + actionName + " not found" , TraceLevel.Warning);

            return null;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
        } catch (IOException e) {
            throw new ParseException("can't parse result for action " + className,trace,TraceLevel.Warning,e);
        } catch (InvocationTargetException e) {
            trace.logException("InvocationTargetException", e, TraceLevel.Warning);
            throw new InvokeException("InvocationTargetException");
        } catch (IllegalAccessException e) {
            trace.logException("IllegalAccessException", e, TraceLevel.Warning);
            throw new InvokeException("IllegalAccessException");
        } catch (Exception e) {
            trace.logException("Unhandled exception", e, TraceLevel.Warning);
            throw new PlatformException("exception");
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

            return null;
        } catch (ClassNotFoundException e) {
            throw new ClassNameNotFound(className,trace);
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


        return al != null && al.isAccessRead(user);

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

        return al != null && al.isAccessCreate(user);

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

        return al != null && al.isAccessEdit(user);

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

        return al != null && al.isAccessRemove(user);

    }

    private boolean checkUserList(User user, List<User> users) {
        return users != null && users.contains(user);
    }
}