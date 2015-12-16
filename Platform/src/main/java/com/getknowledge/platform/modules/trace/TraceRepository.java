package com.getknowledge.platform.modules.trace;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("TraceRepository")
public class TraceRepository extends BaseRepository<Trace> {
    @Override
    protected Class<Trace> getClassEntity() {
        return Trace.class;
    }
}
