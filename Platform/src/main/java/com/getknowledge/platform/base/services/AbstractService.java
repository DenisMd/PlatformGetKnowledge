package com.getknowledge.platform.base.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractService {
    @PersistenceContext
    public EntityManager entityManager;
}
