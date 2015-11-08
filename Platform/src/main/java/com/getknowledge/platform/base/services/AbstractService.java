package com.getknowledge.platform.base.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractService {
    @PersistenceContext
    public EntityManager entityManager;
    public ObjectMapper objectMapper = new ObjectMapper();
}
