package com.getknowledge.platform.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.exceptions.ParseException;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfoRepository;
import com.getknowledge.platform.modules.bootstrapInfo.states.BootstrapState;
import com.getknowledge.platform.utils.ModuleLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bootstrap")
public class BootstrapController {

    @Autowired
    private BootstrapInfoRepository repository;

    @Autowired
    private ModuleLocator moduleLocator;

    Logger logger = LoggerFactory.getLogger(BootstrapController.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/do" , method = RequestMethod.POST)
    public String bootstrap(@RequestParam("data") String jsonData) throws PlatformException{

        //after bootstrap, that init services
        listServices();

        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };

        try {
            HashMap<String, Object> data = objectMapper.readValue(jsonData, typeRef);
            List<BootstrapService> bootstrapServices = moduleLocator.findAllBootstrapServices();
            bootstrapServices.sort(
                    new Comparator<BootstrapService>() {
                        @Override
                        public int compare(BootstrapService o1, BootstrapService o2) {
                            if (o1.getBootstrapInfo().getOrder() > o2.getBootstrapInfo().getOrder()) {
                                return 1;
                            }

                            if (o1.getBootstrapInfo().getOrder() < o2.getBootstrapInfo().getOrder()) {
                                return -1;
                            }
                            return 0;
                        }
                    }
            );
            for (BootstrapService bootstrapService : bootstrapServices) {
                BootstrapInfo bootstrapInfo = null;
                try {
                    bootstrapInfo = repository.getSingleEntityByFieldAndValue("name", bootstrapService.getBootstrapInfo().getName());
                    if(bootstrapInfo != null && bootstrapInfo.getBootstrapState() == BootstrapState.Completed && !bootstrapInfo.isRepeat()) {
                        continue;
                    } else {
                        bootstrapService.bootstrap(data);
                        bootstrapInfo.setBootstrapState(BootstrapState.Completed);
                        repository.update(bootstrapInfo);
                    }
                } catch (Exception e) {
                    if (bootstrapInfo != null) {
                        bootstrapInfo.setBootstrapState(BootstrapState.Failed);
                        bootstrapInfo.setErrorMessage(e.getMessage());
                        repository.update(bootstrapInfo);
                    }
                }
            }

        } catch (IOException e) {
            logger.warn("parse result exception ", e);
            throw new ParseException("parse result exception");
        }
        return "complete";
    }

    @RequestMapping("/listServices")
    public List<BootstrapInfo> listServices() {
        List<BootstrapInfo> bootstrapInfos = new ArrayList<>(50);
        bootstrapInfos.addAll(moduleLocator.findAllBootstrapServices().stream().map
                (bootstrapInfo -> repository.createIfNotExist(bootstrapInfo.getBootstrapInfo()))
                .collect(Collectors.toList()));

        bootstrapInfos.sort(new Comparator<BootstrapInfo>() {
            @Override
            public int compare(BootstrapInfo o1, BootstrapInfo o2) {
                if (o1.getOrder() > o2.getOrder()) {
                    return 1;
                }

                if (o1.getOrder() < o2.getOrder()) {
                    return -1;
                }
                return 0;
            }
        });

        return bootstrapInfos;
    }
}
