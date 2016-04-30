package com.getknowledge.platform.base.services;

import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.PlatformException;
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

    public void create(BaseRepository baseRepository, AbstractEntity abstractEntity){
        baseRepository.create(abstractEntity,true);
    }

    public void update(BaseRepository baseRepository, AbstractEntity abstractEntity){
        baseRepository.update(abstractEntity,true);
    }

    public void remove(BaseRepository baseRepository, Long id) throws PlatformException {
        baseRepository.remove(id,true);
    }

    public AbstractEntity read(BaseRepository baseRepository, Long id) throws PlatformException {
        return baseRepository.read(id,true);
    }

    public List<AbstractEntity> list(BaseRepository baseRepository) throws PlatformException {
        return baseRepository.list(true);
    }

    public List<AbstractEntity> list(BaseRepository baseRepository,int first, int max) throws PlatformException {
        return baseRepository.listPartial(first,max,true);
    }

    public AbstractEntity prepare(AbstractEntity entity,BaseRepository baseRepository, User currentUser) throws Exception {
        return baseRepository.prepare(entity,baseRepository,currentUser,null);
    }

    public void merge(AbstractEntity entity,BaseRepository baseRepository) {
        baseRepository.merge(entity);
    }
}
