model.controller("programCtrl", function ($scope,applicationService,className,pageService,$state) {
    var programId = pageService.getPathVariable("program",$state.params.path);
    $scope.program = {
        tagsName : []
    };

    function readProgram(){
        applicationService.read($scope,"program",className.program,programId,function(program){
            if (!("tags" in program)) {
                program.tagsName = [];
            } else {
                program.tagsName = [];
                program.tags.forEach(function(element){
                    program.tagsName.push(element.tagName);
                })
            }

            $scope.program.urls = [];
            program.links.forEach(function (item) {
                $scope.program.urls.push({
                    name : item
                });
            });
        });
    };

    readProgram();
    $scope.programImg = function(){
        return applicationService.imageHref(className.program,programId);
    };

    $scope.addUrl = function() {
        $scope.program.urls.push({
            name : ""
        });
    };

    $scope.removeUrl = function(index) {
        $scope.program.urls.splice(index,1);
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
        croppedImg.src = applicationService.imageHref(className.program,$scope.program.id);
        croppedImg.notUseDefault = $scope.program.imageViewExist;
        return croppedImg;
    };

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"",className.program,"uploadCover",{programId:$scope.program.id},file);
    };

    $scope.uploader = applicationService.createUploader($scope,"",className.program,"uploadData",{programId:+programId});

    $scope.programData = function(){
        return applicationService.fileByKeyHref(className.program,programId,"key");
    };


});