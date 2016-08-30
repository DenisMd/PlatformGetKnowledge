model.controller("courseCtrl", function ($scope,$timeout,$languages,applicationService,className,pageService,$state) {

    //------------------------------------------ Иницализация перменных
    var courseId = pageService.getPathVariable("course",$state.params.path);

    $scope.course = {
        tagsName : []
    };

    //Первое видео
    $scope.introVideo = {
        showComments : false,
        eventId : "introVideo"
    };

    $scope.descriptionScroll = {
        theme: 'dark-3',
        setHeight: 450,
        axis: "y",
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: true
        }
    };

    $scope.knowledgesResult = [];

    $scope.langs = $languages.languages;

    $scope.videoIsPresent = false;

    //-------------------------------------- Чтение курса и уроков
    function readCourse(){
        applicationService.read($scope,"course",className.course,courseId,function(course){
            if (!("tags" in course)) {
                course.tagsName = [];
            } else {
                course.tagsName = [];
                course.tags.forEach(function(element){
                    course.tagsName.push(element.tagName);
                });
            }

            if (course.item || course.item.price) {
                course.item.price = $scope.convertPrice(course.item.price);
            }

            course.imageSrc = applicationService.imageHref(className.course,course.id);

            course.sourceKnowledge.forEach(function(knowledge) {
                knowledge.image = applicationService.imageHref(className.knowledge,knowledge.id);
            });

            course.requiredKnowledge.forEach(function(knowledge) {
                knowledge.image = applicationService.imageHref(className.knowledge,knowledge.id);
            });

            course.sourceKnowledge.forEach(function (item) {
               item.knowldgeHref = $scope.createUrl("/knowledge/" + item.id);
            });

            course.requiredKnowledge.forEach(function (item) {
                item.knowldgeHref = $scope.createUrl("/knowledge/" + item.id);
            });

            if (course.author){
                course.author.imageSrc = $scope.userImg(course.author.id);
                course.author.userUrl = $scope.createUrl("/user/"+course.author.id);
            }


            if (course.intro) {
                $scope.introVideo.id = course.intro.id;
                $scope.$broadcast("video" + $scope.introVideo.eventId.capitalizeFirstLetter() + "Event");
            }

            readTutorials();

            applicationService.action($scope,"userHasAccessToCourse",className.course,"userHasAccessToCourse",{
                courseId : +course.id
            });

            if (course.editable) {

                applicationService.action($scope, "knowledges", className.knowledge, "getKnowledgeByType", {
                    sectionName: course.groupCourses.section.name
                }, function (knowledge) {
                    knowledge.image = applicationService.imageHref(className.knowledge, knowledge.id);
                });

                course.language = $languages.getLanguageByName(course.language.name);

                updateCourseImage(course);
                updateVideoImage(course);

                $scope.videoIsPresent = !angular.isUndefined(course.intro);
            }
        });
    }

    function readTutorials() {
        applicationService.action($scope,"tutorials",className.course,"getTutorialsForCourse",{
            courseId : +courseId
        },function(tutorial) {
            for (var key in tutorial) {
                tutorial[key].durationTime = new Date(1970, 0, 1);
                if (!angular.isUndefined(tutorial.duration)) {
                    tutorial[key].durationTime.setMilliseconds(tutorial[key].duration);
                }
                tutorial[key].link = $scope.addUrlToPath('/tutorial/'+key);
            }
        });
    }

    readCourse();

    //-------------------------------------- Изменение ифнорамации о курсе
    $scope.showEditableContent = false;

    $scope.changeEditableContent = function () {
        $scope.showEditableContent = !$scope.showEditableContent;
    };

    $scope.updateCourse = function(course) {
        var result = {};
        result.courseId = course.id;
        result.name = course.name;
        result.description = course.description;

        result.tags = course.tagsName;
        result.language = course.language.capitalizeFirstLetter();
        result.sourceKnowledge = [];
        result.requiredKnowledge = [];

        course.sourceKnowledge.forEach(function(knowledge){
            result.sourceKnowledge.push(knowledge.id);
        });

        course.requiredKnowledge.forEach(function(knowledge){
            result.requiredKnowledge.push(knowledge.id);
        });

        applicationService.action($scope,"",className.course,"updateCourseInformation",result,function(result){
            $scope.showToast($scope.getResultMessage(result));
            readCourse();
        });
    };

    $scope.querySearch = function(criteria) {
        return $scope.knowledges.filter(createFilterFor(criteria));
    };

    function createFilterFor(query) {
        return function filterFn(contact) {
            return (contact.name.indexOf(query) !== -1);
        };
    }

    //-------------------------------------- Работа с изображением
    $scope.courseImage = {
        id : 'course-cover',
        save: function(file){
            uploadImage(file);
        },
        areaType:"square"
    };

    var uploadImage = function(file) {
        applicationService.actionWithFile($scope,"",className.course,"uploadCover",{courseId:$scope.course.id},file,function (result) {
            $scope.showToast($scope.getResultMessage(result));
            if (result.status === "Complete") {
                $scope.course.imageViewExist = true;
                $scope.$broadcast("updateCropImage"+$scope.courseImage.id+"Event");
            }
        });
    };

    function updateCourseImage(course){
        $scope.courseImage.src = applicationService.imageHref(className.course,course.id);
        $scope.courseImage.notUseDefault = course.imageViewExist;

        //Если изображение открывается первый раз событие не сработает так не зарегестрированно
        //Поэтому добавляется проверка для открытия
        $scope.courseImage.setupImgae = true;

        $scope.$broadcast("updateCropImage"+$scope.courseImage.id+"Event");
    }

    //-------------------------------------- Работа с видео
    $scope.videoImg = {
        id : 'video-intro-img',
        save: function(file){
            uploadIntro(file);
        },
        areaType:"square"
    };

    var uploadIntro = function(file) {
        applicationService.actionWithFile($scope,"",className.course,"uploadVideoIntro",{
            courseId : $scope.course.id,
            videoName : $scope.course.intro.videoName
        },file,function (result) {
            $scope.showToast($scope.getResultMessage(result));
            if (result.status === "Complete") {
                $scope.course.intro.imageViewExist = true;
                $scope.$broadcast("updateCropImage"+$scope.videoImg.id+"Event");
                readCourse();
            }
        });
    };

    function updateVideoImage(course){
        if (course.intro) {
            $scope.videoImg.src = applicationService.imageHref(className.video, course.intro.id);
            $scope.videoImg.notUseDefault = course.intro.imageViewExist;

            //Если изображение открывается первый раз событие не сработает так не зарегестрированно
            //Поэтому добавляется проверка для открытия
            $scope.videoImg.setupImgae = true;

            $scope.$broadcast("updateCropImage" + $scope.videoImg.id + "Event");
        }
    }

    $scope.uploadIntroData = {
        btnTitle : "course_intro",
        multiplyFiles : false,
        className : className.video,
        actionName : "uploadVideo",
        title : "course_intro",
        prepareParams : function(formData){
            formData.data = JSON.stringify({videoId:$scope.course.intro.id});
        },
        maxFileSize : 512000
    };

    //-------------------------------------- Работа с уроками
    $scope.showAdvanced = function(ev) {
        $scope.showDialog(ev,$scope,"createTutorial.html",function(answer){
            var request = {};
            request.courseId = $scope.course.id;
            request.name = answer.name;
            applicationService.action($scope,"", className.tutorial,"createTutorial",request,function(result){
                $scope.showToast(result);
                readTutorials();
            });
        });
    };

    //-------------------------------------- Создание релиза
    $scope.makeRelease = function () {
        applicationService.action($scope,"",className.course,"release", {
            courseId : $scope.course.id,
            version : 0
        },function(result){
            $scope.showToast(result);
            readCourse();
        });
    };
});