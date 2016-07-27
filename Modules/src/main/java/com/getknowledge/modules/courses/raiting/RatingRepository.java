package com.getknowledge.modules.courses.raiting;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.stereotype.Repository;

@Repository("RatingRepository")
public class RatingRepository extends ProtectedRepository<Rating> {
    @Override
    protected Class<Rating> getClassEntity() {
        return Rating.class;
    }

    @Override
    public void create(Rating object) {
        double avg = (object.getQualityExercises() + object.getQualityInformation() + object.getQualityTest() + object.getRelevanceInformation()) / 4.0;
        object.setAvgRating(avg);
        super.create(object);
    }
}
