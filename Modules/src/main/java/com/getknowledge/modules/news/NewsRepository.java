package com.getknowledge.modules.news;


import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository("NewsRepository")
public class NewsRepository extends BaseRepository<News> {


    @Override
    public void create(News object) {
        object.setPostDate(Calendar.getInstance());
        super.create(object);
    }

    @Override
    protected Class<News> getClassEntity() {
        return News.class;
    }
}
