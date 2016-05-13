package com.getknowledge.platform.modules.trace;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.enumerations.RepOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("TraceRepository")
public class TraceRepository extends BaseRepository<Trace> {

    @Override
    public List<RepOperations> restrictedOperations() {
        List<RepOperations> operations = new ArrayList<>();
        operations.add(RepOperations.Create);
        operations.add(RepOperations.Update);
        operations.add(RepOperations.Remove);
        return operations;
    }

    @Override
    protected Class<Trace> getClassEntity() {
        return Trace.class;
    }
}
