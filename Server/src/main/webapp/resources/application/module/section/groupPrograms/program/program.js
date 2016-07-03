model.controller("programCtrl", function ($scope,applicationService,className,pageService,$state,$languages) {
    var programId = pageService.getPathVariable("program",$state.params.path);
    $scope.program = {
        tagsName : []
    };

    function readProgram(){
        applicationService.read($scope,"program",className.program,programId,function(program){
            if (!program) {
                $state.go("404");
            }
            if (!("tags" in program)) {
                program.tagsName = [];
            } else {
                program.tagsName = [];
                program.tags.forEach(function(element){
                    program.tagsName.push(element.tagName);
                });
            }

            program.urls = [];
            program.links.forEach(function (item) {
                program.urls.push({
                    name : item
                });
            });

            program.coverUrl = applicationService.imageHref(className.program,program.id);
            program.downloadUrl = applicationService.fileByKeyHref(className.program,program.id,"key");
            program.language = program.language.name.toLowerCase();

            if (program.owner){
                program.owner.imageSrc = $scope.userImg(program.owner.id);
                program.owner.userUrl = $scope.createUrl("/user/"+program.owner.id);
            }

            updateCroppedImage(program);
        });
    }

    readProgram();

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
        result.authorName = program.authorName;
        result.description = program.description;
        result.language = program.language.capitalizeFirstLetter();
        result.links = [];
        program.urls.forEach(function(element){
            result.links.push(element.name);
        });

        result.tags = program.tagsName;
        applicationService.action($scope,"",className.program,"updateProgramInformation",result,function(result){
            $scope.showToast($scope.getResultMessage(result));
            readProgram();
        });
    };

    $scope.croppedImg = {
        id : 'program-cover',
        save: function(file){
            updateImage(file);
        },
        areaType:"square"
    };

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"cover",className.program,"uploadCover",{programId:$scope.program.id},file,function (result) {
            $scope.showToast($scope.getResultMessage(result));
            if (result.status === "Complete") {
                $scope.program.imageViewExist = true;
                $scope.$broadcast("updateCropImage"+$scope.croppedImg.id+"Event");
            }
        });
    };

    function updateCroppedImage(program){
        $scope.croppedImg.src = applicationService.imageHref(className.program,program.id);
        $scope.croppedImg.notUseDefault = program.imageViewExist;

        //Если изображение открывается первый раз событие не сработает так не зарегестрированно
        //Поэтому добавляется проверка для открытия
        $scope.croppedImg.setupImgae = true;

        $scope.$broadcast("updateCropImage"+$scope.croppedImg.id+"Event");
    }

    $scope.langs = $languages.languages;

    $scope.uploadData = {
        btnTitle : "program_data",
        multiplyFiles : false,
        className : className.program,
        actionName : "uploadData",
        title : "program_data",
        parameters : {programId:+programId},
        maxFileSize : 204800
    };

    $scope.showDeleteDialog = function(ev) {
        $scope.showConfirmDialog(
            ev,
            $scope.translate("program_delete_dialog_title") + " " + $scope.program.name,
            "",
            'Delete program',
            $scope.translate("delete"),
            $scope.translate("cancel"),
            function () {
                applicationService.remove($scope,"",className.program,$scope.program.id,function (result) {
                    $scope.showToast($scope.getResultMessage(result));
                });
            }
        )
    }

    //Кооментарии к книгам
    $scope.commentData = {
        id : "ProgramComment",
        commentClassName : className.programComment,
        filedName : "program.id",
        objectId : parseInt(programId),
        withoutEvent : true
    };


});