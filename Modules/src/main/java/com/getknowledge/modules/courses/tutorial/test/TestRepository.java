package com.getknowledge.modules.courses.tutorial.test;

import com.getknowledge.modules.courses.tutorial.test.question.Question;
import com.getknowledge.modules.courses.tutorial.test.question.QuestionRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("TestRepository")
public class TestRepository extends ProtectedRepository<Test> {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    protected Class<Test> getClassEntity() {
        return Test.class;
    }

    @Override
    public void remove(Test entity) {
        for (Question question : entity.getQuestionList()) {
            questionRepository.remove(question);
        }
        super.remove(entity);
    }
}
