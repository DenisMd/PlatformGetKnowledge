package com.getknowledge.modules.courses.raiting;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("RatingRepository")
public class RatingRepository extends BaseRepository<Rating> {
    @Override
    protected Class<Rating> getClassEntity() {
        return Rating.class;
    }
}
