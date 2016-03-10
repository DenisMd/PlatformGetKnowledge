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
                })
            }
        });
    };

    readCourse();
    $scope.courseImg = function(){
        return applicationService.imageHref(className.course,courseId);
    };

    $scope.showEditableContent = false;

    $scope.updateProgram = function(program) {
        var result = {};
        result.programId = program.id;
        result.name = program.name;
        result.description = program.description;
        result.links = [];
        program.urls.forEach(function(element){
            result.links.push(element.name);
        });

        result.tags = program.tagsName;
        applicationService.action($scope,"",className.program,"updateProgramInformation",result,function(result){
            $scope.showToast(result);
            readProgram();
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
        applicationService.actionWithFile($scope,"",className.course,"uploadCover",{programId:$scope.program.id},file);
    };

});