package com.getknowledge.platform.utils;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.exceptions.ModuleNotFound;
import com.getknowledge.platform.modules.trace.TraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class  ModuleLocator {

    @Autowired
    private ApplicationContext ac;

    @Autowired
    private TraceService trace;

    public BaseRepository findRepository(Class classEntity) throws ModuleNotFound {
        if (classEntity == null) return null;
        ModuleInfo moduleInfo = (ModuleInfo) classEntity.getAnnotation(ModuleInfo.class);
        if(moduleInfo == null) {
            throw new ModuleNotFound("Annotation \"ModuleInfo\" not found for entity : " + classEntity.getName(),trace);
        }
        BaseRepository baseRepository = (BaseRepository) ac.getBean(moduleInfo.repositoryName());
        return baseRepository;
    }

    public AbstractService findService(Class classEntity) throws ModuleNotFound {
        if (classEntity == null) return null;
        ModuleInfo moduleInfo = (ModuleInfo) classEntity.getAnnotation(ModuleInfo.class);
        if(moduleInfo == null) {
            throw new ModuleNotFound("Annotation \"ModuleInfo\" not found for entity : " + classEntity.getName(),trace);
        }
        AbstractService abstractRepository = (AbstractService) ac.getBean(moduleInfo.serviceName());
        return abstractRepository;
    }

    public AbstractService findService(String serviceName) throws ModuleNotFound {
        AbstractService abstractRepository = (AbstractService) ac.getBean(serviceName);
        return abstractRepository;
    }

    public BaseRepository findRepository(String repositoryName) throws ModuleNotFound {
        BaseRepository baseRepository = (BaseRepository) ac.getBean(repositoryName);
        return baseRepository;
    }

    public List<BootstrapService> findAllBootstrapServices(){
        List<BootstrapService> bootstraps = new ArrayList<>();
        ac.getBeansWithAnnotation(Service.class).entrySet().stream().filter(b -> b.getValue() instanceof BootstrapService).forEach(b -> {
            BootstrapService info = ((BootstrapService) b.getValue());
            bootstraps.add(info);
        });
        return bootstraps;
    }
}

