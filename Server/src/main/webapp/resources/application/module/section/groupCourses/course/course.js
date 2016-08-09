model.controller("courseCtrl", function ($scope,applicationService,className,pageService,$state) {
    var courseId = pageService.getPathVariable("course",$state.params.path);
    $scope.course = {
        tagsName : []
    };

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
        });
    }

    readCourse();
    //Первое видео
    $scope.indexVideo1 = {
        id : 1,
        showComments : false
    };

    $scope.showEditableContent = false;

    $scope.updateCourse = function(course) {
        var result = {};
        result.courseId = course.id;
        result.name = course.name;
        result.description = course.description;

        result.tags = course.tagsName;
        result.sourceKnowledge = [];
        result.requiredKnowledge = [];

        course.sourceKnowledge.forEach(function(knowledge){
            result.sourceKnowledge.push(knowledge.id);
        });

        course.requiredKnowledge.forEach(function(knowledge){
            result.requiredKnowledge.push(knowledge.id);
        });

        applicationService.action($scope,"",className.course,"updateCourseInformation",result,function(result){
            $scope.showToast(result);
            readCourse();
        });
    };

    var croppedImg = {
        save: function(file){
            updateImage(file);
        },
        areaType:"square"
    };

    $scope.getCropImageData  = function(){
        croppedImg.src = applicationService.imageHref(className.course,$scope.course.id);
        croppedImg.notUseDefault = $scope.course.imageViewExist;
        return croppedImg;
    };

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"",className.course,"uploadCover",{courseId:$scope.course.id},file);
    };


    var videoImg = {
        save: function(file){
            updateVideoImage(file);
        },
        areaType:"square"
    };

    $scope.getVideoImage  = function(){
        if ($scope.course.intro) {
            videoImg.src = applicationService.imageHref(className.video, $scope.course.intro.id);
            videoImg.notUseDefault = true;
        }
        return videoImg;
    };

    var updateVideoImage = function(file) {
        applicationService.actionWithFile($scope,"",className.course,"uploadVideoIntro",{
            courseId : $scope.course.id,
            videoName : $scope.course.intro.videoName
        },file);
    };


    applicationService.list($scope,"knowledges",className.knowledge , function(knowledge) {
        knowledge.image = applicationService.imageHref(className.knowledge,knowledge.id);
    });

    $scope.knowledgesResult = [];

    function createFilterFor(query) {
        return function filterFn(contact) {
            return (contact.name.indexOf(query) !== -1);
        };
    }

    $scope.querySearch = function(criteria) {
       return $scope.knowledges.filter(createFilterFor(criteria));
    };

    $scope.uploader = applicationService.createUploader($scope,"",className.video,"uploadVideo",null,null,function(formData){
        formData.data = JSON.stringify({videoId:$scope.course.intro.id});
    });

    function readTutorials() {
        applicationService.action($scope,"tutorials",className.course,"getTutorialsForCourse",{
            courseId : +courseId
        });
    }

    readTutorials();

    $scope.showAdvanced = function(ev) {
        $scope.showDialog(ev,$scope,"createTutorial.html",function(answer){
            var request = {};
            request.courseId = $scope.course.id;
            request.name = answer.name;
            applicationService.action($scope,"", className.course,"createTutorial",request,function(result){
                $scope.showToast(result);
                readTutorials();
            });
        });
    };

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