package com.getknowledge.platform.base.services;

import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service("CrudService")
public class CrudService {

    public void create(BaseRepository baseRepository, AbstractEntity abstractEntity){
        baseRepository.create(abstractEntity);
    }

    public void update(BaseRepository baseRepository, AbstractEntity abstractEntity){
        baseRepository.update(abstractEntity);
    }

    public void remove(BaseRepository baseRepository, Long id) throws PlatformException {
        baseRepository.remove(id);
    }

    public AbstractEntity read(BaseRepository baseRepository, Long id) throws PlatformException {
        return baseRepository.read(id);
    }

    public List<AbstractEntity> list(BaseRepository baseRepository) throws PlatformException {
        return baseRepository.list();
    }

    public List<AbstractEntity> list(BaseRepository baseRepository,int first, int max) throws PlatformException {
        return baseRepository.listPartial(first,max);
    }

    public AbstractEntity prepare(AbstractEntity entity,BaseRepository baseRepository, User currentUser) throws Exception {
        return baseRepository.prepare(entity,baseRepository,currentUser,null);
    }

    public void merge(AbstractEntity entity,BaseRepository baseRepository) {
        baseRepository.merge(entity);
    }
}
