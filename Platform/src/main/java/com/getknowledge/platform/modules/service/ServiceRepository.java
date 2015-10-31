package com.getknowledge.platform.modules.service;

import com.getknowledge.platform.base.repositories.TransientRepository;
import com.getknowledge.platform.initializer.InitApplication;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;

@Repository("ServiceRepository")
public class ServiceRepository extends TransientRepository<Service> {

    @Autowired
    InitApplication application;

    @PostConstruct
    public void ServiceRepository(){

        for (String serviceName : application.getServices().keySet()) {
            Service service = new Service();
            service.setName(serviceName);
            super.list.add(service);
        }
    }
}
