package com.getknowledge.platform.modules.service;

import com.getknowledge.platform.base.repositories.TransientRepository;
import com.getknowledge.platform.initializer.InitApplication;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@Repository("ServiceRepository")
public class ServiceRepository extends TransientRepository<Service> {

}
