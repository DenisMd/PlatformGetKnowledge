package com.getknowledge.platform.base.services;

import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.enumerations.RepOperations;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.exceptions.RestrictedException;
import com.getknowledge.platform.modules.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 *
 * Данный сервис используется для исполнения операций внутри транзакций через Api rest-server-а
 *
 * */
@Transactional
@Service("CrudService")
public class CrudService {

    public void create(BaseRepository baseRepository, AbstractEntity abstractEntity) throws RestrictedException {
        if (baseRepository.restrictedOperations().contains(RepOperations.Create)) {
            throw new RestrictedException("Operation create is restricted");
        }
        baseRepository.create(abstractEntity);
    }

    public void update(BaseRepository baseRepository, AbstractEntity abstractEntity) throws RestrictedException {
        if (baseRepository.restrictedOperations().contains(RepOperations.Update)) {
            throw new RestrictedException("Operation update is restricted");
        }
        baseRepository.update(abstractEntity);
    }

    public void remove(BaseRepository baseRepository, Long id) throws PlatformException {
        if (baseRepository.restrictedOperations().contains(RepOperations.Remove)) {
            throw new RestrictedException("Operation remove is restricted");
        }
        baseRepository.remove(id);
    }

    public AbstractEntity read(BaseRepository baseRepository, Long id) throws PlatformException {
        if (baseRepository.restrictedOperations().contains(RepOperations.Read)) {
            throw new RestrictedException("Operation read is restricted");
        }
        return baseRepository.read(id);
    }

    public List<AbstractEntity> list(BaseRepository baseRepository) throws PlatformException {
        if (baseRepository.restrictedOperations().contains(RepOperations.Read)) {
            throw new RestrictedException("Operation list is restricted");
        }
        return baseRepository.list();
    }

    public List<AbstractEntity> list(BaseRepository baseRepository,int first, int max) throws PlatformException {
        if (baseRepository.restrictedOperations().contains(RepOperations.Read)) {
            throw new RestrictedException("Operation list partial is restricted");
        }
        return baseRepository.listPartial(first,max,true);
    }

    public AbstractEntity prepare(AbstractEntity entity,BaseRepository baseRepository, User currentUser) throws Exception {
        return baseRepository.prepare(entity,baseRepository,currentUser,null);
    }

    public void merge(AbstractEntity entity,BaseRepository baseRepository) {
        baseRepository.merge(entity);
    }
}
