package com.getknowledge.modules.courses.tutorial.homeworks;

import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository("HomeWorkRepository")
public class HomeWorkRepository extends ProtectedRepository<HomeWork> {
    @Override
    protected Class<HomeWork> getClassEntity() {
        return HomeWork.class;
    }

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public void remove(HomeWork entity) {
        if (entity.getVideo() != null) {
            videoRepository.remove(entity.getVideo().getId());
        }

        super.remove(entity);
    }

    public HomeWork createHomeWork(Tutorial tutorial, String name) {
        HomeWork homeWork = new HomeWork();
        homeWork.setName(name);
        homeWork.setTutorial(tutorial);
        homeWork.setLastChangeTime(Calendar.getInstance());
        create(homeWork);
        return homeWork;
    }
}
