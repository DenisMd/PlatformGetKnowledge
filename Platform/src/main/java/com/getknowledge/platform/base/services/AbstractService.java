package com.getknowledge.platform.base.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.modules.role.names.RoleName;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;

public abstract class AbstractService {

    @Autowired
    protected UserRepository userRepository;

    @PersistenceContext
    public EntityManager entityManager;
    public ObjectMapper objectMapper = new ObjectMapper();

    public boolean isAccessToRead(HashMap<String,Object> data, AbstractEntity abstractEntity) {
        String principalName = (String) data.get("principalName");
        if (principalName == null || principalName.isEmpty()) {
            return  false;
        }

        User currentUser = userRepository.getSingleEntityByFieldAndValue("login" , principalName);

        if (currentUser.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            return true;
        }

        return abstractEntity.getAuthorizationList() != null && abstractEntity.getAuthorizationList().isAccessRead(currentUser);

    }

    public boolean isAccessToEdit(HashMap<String,Object> data, AbstractEntity abstractEntity) {
        String principalName = (String) data.get("principalName");
        if (principalName == null || principalName.isEmpty()) {
            return  false;
        }

        User currentUser = userRepository.getSingleEntityByFieldAndValue("login" , principalName);

        if (currentUser.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            return true;
        }

        return abstractEntity.getAuthorizationList() != null && abstractEntity.getAuthorizationList().isAccessEdit(currentUser);

    }

    public Long longFromField(String key, HashMap<String, Object> data) {
        return new Long((Integer)data.get(key));
    }

}
