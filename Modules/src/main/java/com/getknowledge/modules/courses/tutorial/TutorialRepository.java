package com.getknowledge.modules.courses.tutorial;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.DeleteException;
import com.getknowledge.platform.exceptions.PlatformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository("TutorialRepository")
public class TutorialRepository extends BaseRepository<Tutorial> {
    @Override
    protected Class<Tutorial> getClassEntity() {
        return Tutorial.class;
    }

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public void remove(Long id) throws PlatformException {

        Tutorial tutorial = read(id);
        if (tutorial == null) throw new DeleteException("Can't find tutorial by id : " + id);

        if (tutorial.getVideo() != null) {
            videoRepository.remove(tutorial.getVideo().getId());
        }

        super.remove(id);
    }
}
