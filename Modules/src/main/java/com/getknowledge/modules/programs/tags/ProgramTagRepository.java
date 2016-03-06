package com.getknowledge.modules.programs.tags;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ProgramTagRepository")
public class ProgramTagRepository extends BaseRepository<ProgramTag> {

    @Autowired
    private TraceService trace;

    @Override
    protected Class<ProgramTag> getClassEntity() {
        return ProgramTag.class;
    }

    public ProgramTag createIfNotExist(String tag) {
        ProgramTag result = getSingleEntityByFieldAndValue("tagName" , tag);

        if (result == null) {
            result = new ProgramTag();
            result.setTagName(tag);
            create(result);
        }

        return result;
    }

    public void removeUnusedTags() {
        List<ProgramTag> list = entityManager.createQuery("select t from ProgramTag  t where t.programs is empty").getResultList();
        list.forEach((tag -> {
            try {
                remove(tag.getId());
            } catch (PlatformException e) {
                trace.logException("Error remove program tag" , e, TraceLevel.Error);
            }
        }));
    }
}
