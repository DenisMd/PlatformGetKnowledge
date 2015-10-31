package com.getknowledge.platform.modules.trace;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.repository.AbstractRepository;

@Repository("TraceRepository")
public class TraceRepository extends BaseRepository<Trace> {

}
