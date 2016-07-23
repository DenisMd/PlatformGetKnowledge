package com.getknowledge.modules.courses;

import com.getknowledge.modules.books.group.GroupBooks;
import com.getknowledge.modules.courses.changelist.ChangeList;
import com.getknowledge.modules.courses.changelist.ChangeListRepository;
import com.getknowledge.modules.courses.group.GroupCourses;
import com.getknowledge.modules.courses.raiting.Rating;
import com.getknowledge.modules.courses.raiting.RatingRepository;
import com.getknowledge.modules.courses.tags.CoursesTag;
import com.getknowledge.modules.courses.tags.CoursesTagRepository;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.courses.tutorial.TutorialRepository;
import com.getknowledge.modules.courses.tutorial.homeworks.HomeWork;
import com.getknowledge.modules.courses.tutorial.homeworks.HomeWorkRepository;
import com.getknowledge.modules.courses.tutorial.test.Test;
import com.getknowledge.modules.courses.tutorial.test.TestRepository;
import com.getknowledge.modules.courses.tutorial.test.question.Question;
import com.getknowledge.modules.courses.tutorial.test.question.QuestionRepository;
import com.getknowledge.modules.courses.tutorial.test.question.answer.Answer;
import com.getknowledge.modules.courses.tutorial.test.question.answer.AnswerRepository;
import com.getknowledge.modules.courses.version.Version;
import com.getknowledge.modules.dictionaries.knowledge.Knowledge;
import com.getknowledge.modules.dictionaries.knowledge.KnowledgeRepository;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.programs.Program;
import com.getknowledge.modules.shop.item.Item;
import com.getknowledge.modules.shop.item.ItemRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.video.Video;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.platform.annotations.Filter;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.FilterCountQuery;
import com.getknowledge.platform.base.repositories.FilterQuery;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.exceptions.DeleteException;
import com.getknowledge.platform.exceptions.PlatformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository("CourseRepository")
public class CourseRepository extends ProtectedRepository<Course> {
    @Override
    protected Class<Course> getClassEntity() {
        return Course.class;
    }

    @Autowired
    private TutorialRepository tutorialRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ChangeListRepository changeListRepository;

    @Autowired
    private CoursesTagRepository coursesTagRepository;

    @Autowired
    private KnowledgeRepository knowledgeRepository;

    @Autowired
    private HomeWorkRepository homeWorkRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Filter(name = "searchCourses")
    public void searchCourses(HashMap<String,Object> data , FilterQuery<Course> query, FilterCountQuery<Course> countQuery) {
        Join join = query.getJoin(new String[]{"tags"},0,null, JoinType.LEFT);
        String value = (String) data.get("textValue");
        Predicate name = query.getCriteriaBuilder().like(query.getRoot().get("name"),"%"+value+"%");
        Predicate tags = query.getCriteriaBuilder().like(join.get("tagName"),"%"+value+"%");
        query.addPrevPredicate(query.getCriteriaBuilder().or(name,tags));


        Join join2 = countQuery.getJoin(new String[]{"tags"}, 0, null, JoinType.LEFT);
        Predicate name2 = countQuery.getCriteriaBuilder().like(countQuery.getRoot().get("name"),"%"+value+"%");
        Predicate tags2 = countQuery.getCriteriaBuilder().like(join2.get("tagName"),"%"+value+"%");
        countQuery.addPrevPredicate(countQuery.getCriteriaBuilder().or(name2,tags2));
    }

    @Filter(name = "isFreeCourses")
      public void isFreeCourses(HashMap<String,Object> data , FilterQuery<Course> query, FilterCountQuery<Course> countQuery) {
        Join priceJoin = query.getRoot().join("item").join("price");
        Predicate freePrice = query.getCriteriaBuilder().equal(priceJoin.get("free"),true);
        query.addPrevPredicate(freePrice);

        Join priceJoin2 = countQuery.getRoot().join("item").join("price");
        Predicate freePrice2 = countQuery.getCriteriaBuilder().equal(priceJoin2.get("free"),true);
        countQuery.addPrevPredicate(freePrice2);
    }

    @Filter(name = "isAvailable")
    public void isAvailable(HashMap<String,Object> data , FilterQuery<Course> query, FilterCountQuery<Course> countQuery) {
        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (currentUser == null)
            return;
        Join knowledgeJoin = query.getJoin(new String[]{"requiredKnowledge"},0,null,JoinType.INNER);
        //query.getCriteriaBuilder().
    }

    @Filter(name = "orderByPrice")
    public void orderByPrice(HashMap<String,Object> data , FilterQuery<Course> query, FilterCountQuery<Course> countQuery) {
        //Пример
//        Join join = query.getRoot().join("books", JoinType.LEFT);
//        query.getCriteriaQuery().groupBy(query.getRoot().get("id"));
//        boolean desc = (boolean) data.get("desc");
//        if (desc) {
//            query.getCriteriaQuery().orderBy(query.getCriteriaBuilder().desc(query.getCriteriaBuilder().count(join)));
//        } else {
//            query.getCriteriaQuery().orderBy(query.getCriteriaBuilder().asc(query.getCriteriaBuilder().count(join)));
//        }
    }

    @Filter(name = "orderByRating")
    public void orderByRating(HashMap<String,Object> data , FilterQuery<Course> query, FilterCountQuery<Course> countQuery) {
        //Сделать
    }


    private void removeCourseInfo(Course course) {
        if (course.getTutorials() != null) {
            for (Tutorial tutorial : course.getTutorials()) {
                tutorialRepository.remove(tutorial.getId());
            }
        }

        if (course.getIntro() != null) {
            videoRepository.remove(course.getIntro().getId());
        }

        if (course.getChangeLists() != null) {
            for (ChangeList changeList : course.getChangeLists()) {
                changeListRepository.read(changeList.getId());
            }
        }

        if (course.getTags() != null) {
            coursesTagRepository.removeTagsFromEntity(course);
            coursesTagRepository.removeUnusedTags();
            merge(course);
        }

    }

    @Override
    public void remove(Course course) {
        //Удаляем черновик
        if (course.getBaseCourse() != null) {
            removeCourseInfo(course);
            super.remove(course);

        } else {
            if (course.getDraftCourse() != null) {
                remove(course.getDraftCourse().getId());
            }

            removeCourseInfo(course);
            super.remove(course);
        }
    }

    private void addCourseToTag(Course course) {
        for (CoursesTag coursesTag : course.getTags()) {
            coursesTag.getCourses().add(course);
            coursesTagRepository.merge(coursesTag);
        }
    }

    public Course createCourse(UserInfo author,GroupCourses groupCourses,String name,String description,Language language,List<String> tags,boolean base) {
        Course course = new Course();
        course.setAuthor(author);
        course.setGroupCourses(groupCourses);
        course.setName(name);
        course.setDescription(description);
        course.setLanguage(language);
        course.setBase(base);
        course.setCreateDate(Calendar.getInstance());
        course.setRelease(false);

        Item item = new Item();
        itemRepository.create(item);
        course.setItem(item);

        Rating rating = new Rating();
        ratingRepository.create(rating);
        course.setRating(rating);

        course.setVersion(new Version(1,0,0));
        if (tags != null) {
            coursesTagRepository.createTags(tags,course);
        }
        create(course);
        addCourseToTag(course);
        return course;
    }

    public Course updateCourse(Course course,String name,String description,List<String> tags,List<Integer> sourceKnowledgeIds,List<Integer> requiredKnowledgeIds){
        if (name != null)
            course.setName(name);
        if (description != null)
            course.setDescription(description);

        coursesTagRepository.removeTagsFromEntity(course);
        if (tags != null  && !tags.isEmpty()) {
            coursesTagRepository.createTags(tags,course);
        }

        if (sourceKnowledgeIds != null) {
            for (Integer id : sourceKnowledgeIds) {
                Knowledge knowledge = knowledgeRepository.read(new Long(id));
                course.getSourceKnowledge().add(knowledge);
            }
        }

        if (requiredKnowledgeIds != null) {
            for (Integer id : requiredKnowledgeIds) {
                Knowledge knowledge = knowledgeRepository.read(new Long(id));
                course.getRequiredKnowledge().add(knowledge);
            }
        }

        merge(course);
        if (tags != null  && !tags.isEmpty()) {
            addCourseToTag(course);
        }
        coursesTagRepository.removeUnusedTags();

        return course;
    }

    public void releaseBaseCourse(Course course,ChangeList changeList) {
        course.getChangeLists().add(changeList);
        course.setRelease(true);
        merge(course);
    }

    public void mergeVideo (Video to, Video from) {
        to.setCover(from.getCover());
        to.setLink(from.getLink());
        to.setVideoName(from.getVideoName());
        videoRepository.merge(to);
    }

    public void mergeHomeWork (HomeWork to, HomeWork from) {
        to.setName(from.getName());
        to.setData(from.getData());
        to.setLastChangeTime(from.getLastChangeTime());
        mergeVideo(to.getVideo(),from.getVideo());
    }

    public void mergeTutorial (Tutorial to, Tutorial from) {
        to.setData(from.getData());
        to.setName(from.getName());
        to.setOrderNumber(from.getOrderNumber());
        to.setLastChangeTime(from.getLastChangeTime());
        mergeVideo(to.getVideo(), from.getVideo());

        for (HomeWork hw : from.getHomeWorks()) {
            if (hw.getOriginal() != null) {
                if (!hw.isDeleting()) {
                    mergeHomeWork(hw.getOriginal(),hw);
                    homeWorkRepository.merge(hw.getOriginal());
                } else {
                    homeWorkRepository.remove(hw);
                }
            } else {
                HomeWork newHw = new HomeWork();
                newHw.setTutorial(from);
                mergeHomeWork(newHw,hw);
                homeWorkRepository.create(newHw);
            }
        }

        if (from.getTest() != null) {
            if (from.getTest().getOriginalTest() != null) {
                if (from.getTest().getDeleting()) {
                    tutorialRepository.remove(from.getOriginalTutorial());
                } else {
                    mergeTest(from.getTest().getOriginalTest(),from.getTest());
                    testRepository.merge(from.getTest().getOriginalTest());
                }
            } else {
                Test test = new Test();
                mergeTest(test,from.getTest());
                testRepository.create(test);
            }
        }
    }

    public void mergeTest(Test to, Test from) {
        to.setLastChangeTime(from.getLastChangeTime());
        for (Question question : from.getQuestionList()) {
            if (question.getOriginalQuestion() != null) {

                if (question.getDeleting()) {
                    questionRepository.remove(question.getOriginalQuestion());
                } else {
                    mergeQuestion(question.getOriginalQuestion(),question);
                    questionRepository.merge(question.getOriginalQuestion());
                }

            } else {
                Question newQ = new Question();
                newQ.setTest(to);
                mergeQuestion(newQ,question);
                questionRepository.create(newQ);
            }
        }
    }

    public void mergeQuestion(Question to , Question from) {
        to.setQuestion(from.getQuestion());
        to.setLastChangeTime(from.getLastChangeTime());

        for (Answer answer : from.getAnswerList()) {
            if (answer.getOriginalAnswer() != null) {

                if (answer.getDeleting()) {

                } else {

                }

            } else {
                Answer newA = new Answer();
                newA.setQuestion(to);
                mergeAnswer(newA,answer);
                answerRepository.create(newA);
            }
        }
    }

    public void mergeAnswer(Answer to, Answer from) {
        to.setLastChangeTime(from.getLastChangeTime());
        to.setDescription(from.getDescription());
        to.setCorrect(from.isCorrect());
        to.setAnswer(from.getAnswer());
    }

    public void mergeDraft(Course base, Course draft) {

        base.setName(draft.getName());
        base.setCover(draft.getCover());
        base.setDescription(draft.getDescription());

        mergeVideo(base.getIntro() , draft.getIntro());

        coursesTagRepository.removeTagsFromEntity(base);
        coursesTagRepository.createTags(draft.getTags().stream().map(tag -> tag.getTagName()).collect(Collectors.toList()),base);

        for (Tutorial draftTutorial : draft.getTutorials()) {
            if (draftTutorial.getOriginalTutorial() != null) {
                if (!draftTutorial.isDeleting()) {
                    mergeTutorial(draftTutorial.getOriginalTutorial(), draftTutorial);
                    tutorialRepository.merge(draftTutorial.getOriginalTutorial());
                } else {
                    tutorialRepository.remove(draftTutorial.getOriginalTutorial());
                }
            } else {
                Tutorial newTutorial = new Tutorial();
                newTutorial.setCourse(base);
                mergeTutorial(newTutorial,draftTutorial);
                //create Tutorial
                tutorialRepository.create(newTutorial);
            }
        }
    }

    public void createVideo (Video to, Video from) {
        to.setCover(from.getCover());
        to.setLink(from.getLink());
        to.setVideoName(from.getVideoName());
        videoRepository.create(to);
    }

    public void createTutorial (Tutorial to, Tutorial from) {
        to.setData(from.getData());
        to.setName(from.getName());
        to.setOrderNumber(from.getOrderNumber());
        to.setLastChangeTime(from.getLastChangeTime());
        createVideo(to.getVideo(), from.getVideo());

        for (HomeWork homeWork : from.getHomeWorks()) {
            HomeWork draftHomeWork = new HomeWork();
            draftHomeWork.setVideo(new Video());
            createHomeWork(draftHomeWork,homeWork);
            to.getHomeWorks().add(draftHomeWork);
        }

        if (from.getTest() != null) {
            Test test = new Test();
            createTest(test,from.getTest());
            to.setTest(test);
        }

        tutorialRepository.create(to);
    }

    public void createTest(Test to, Test from) {
        to.setOriginalTest(from);
        to.setLastChangeTime(from.getLastChangeTime());

        for (Question question : from.getQuestionList()) {
            Question draftQuestion = new Question();
            createQuestion(draftQuestion,question);
            to.getQuestionList().add(draftQuestion);
        }

        testRepository.create(to);
    }

    public void createQuestion(Question to, Question from) {
        to.setLastChangeTime(from.getLastChangeTime());
        to.setOriginalQuestion(from);
        to.setQuestion(from.getQuestion());

        for (Answer answer : from.getAnswerList()) {
            Answer draftAnswer = new Answer();
            createAnswer(draftAnswer,answer);
            to.getAnswerList().add(draftAnswer);
        }

        questionRepository.create(to);
    }

    public void createAnswer(Answer to, Answer from) {
        to.setAnswer(from.getAnswer());
        to.setCorrect(from.isCorrect());
        to.setDescription(from.getDescription());
        to.setOriginalAnswer(from);
        to.setLastChangeTime(from.getLastChangeTime());

        answerRepository.create(to);
    }

    public void createHomeWork (HomeWork to, HomeWork from) {
        to.setData(from.getData());
        to.setName(from.getName());
        to.setOriginal(from);
        to.setLastChangeTime(from.getLastChangeTime());
        createVideo(to.getVideo(), from.getVideo());
        homeWorkRepository.create(to);
    }

    public void createDraft(Course base, Course draft) {
        draft.setName(base.getName());
        draft.setCover(base.getCover());
        draft.setDescription(base.getDescription());

        Video video = new Video();
        createVideo(video,base.getIntro());
        draft.setIntro(video);

        coursesTagRepository.createTags(base.getTags().stream().map(tag -> tag.getTagName()).collect(Collectors.toList()),base);

        for (Tutorial baseTutorial : base.getTutorials()) {
            Tutorial tutorial = new Tutorial();
            tutorial.setVideo(new Video());
            tutorial.setOriginalTutorial(baseTutorial);
            createTutorial(tutorial,baseTutorial);
        }

        create(draft);
    }
}
