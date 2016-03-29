package com.getknowledge.modules.courses.tutorial.homeworks;

import com.getknowledge.platform.base.repositories.ProtectedRepository;

public class HomeWorkRepository extends ProtectedRepository<HomeWork> {



    @Override
    protected Class<HomeWork> getClassEntity() {
        return HomeWork.class;
    }
}
