model.controller("foldersCtrl", function ($scope,applicationService,className,$mdDialog) {

    $scope.currentClassName = "";
    $scope.selectorData1 = {
        className   : className.groupBooks,
        tableName   :   "folder_group_books",
        loadMoreTitle : "folder_loadMore",
        filters      : [
            {
                title : "id",
                type : "number",
                field : "id"
            },
            {
                title : "name",
                type : "text",
                field : "title",
                default : true
            },{
                title : "section",
                type : "enum",
                field : "section.name",
                constants : [],
                default : true
            },{
                title : "folder_create_date",
                type : "dateTime",
                field : "createDate"
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            },{
                name : "section.name",
                title : "section",
                translate : true,
                orderBy : true
            }, {
                name : "title",
                title : "name",
                orderBy : true
            },{
                name : "url",
                title : "folder_url"
            },{
                name : "createDate",
                title : "folder_create_date",
                orderBy : true,
                filter : "date"
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentGroup = item;
            $scope.currentClassName = className.groupBooks;
            $scope.multiLanguageData.languages = {"ru":  item.descriptionRu, "en":  item.descriptionEn};
            updateCroppedImage();
        },
        actions : [
            {
                icon : "fa-plus",
                color : "#46BE28",
                tooltip : "folder_create",
                actionCallback : function (ev){
                    $scope.showDialog(ev,$scope,"createGroupBooks.html",function(answer){
                        answer.sectionId = parseInt(answer.sectionId);
                        answer.type = "book";
                        applicationService.action($scope,"", className.folder,"createFolder",answer,function(result){
                            $scope.showToast($scope.getResultMessage(result));
                            $scope.$broadcast("updateServerSelector");
                        });
                    });
                }
            }
        ],
        deleteOptions : {
            deleteCallback : function (ev,item) {
                $scope.showConfirmDialog(
                    ev,
                    $scope.translate("folder_delete") + " " + item.title,
                    $scope.translate("folder_delete_confirmation"),
                    'Delete folder',
                    $scope.translate("delete"),
                    $scope.translate("cancel"),
                    function () {
                        applicationService.remove($scope,"",className.groupBooks,$scope.currentGroup.id,function (result) {
                            $scope.showToast($scope.getResultMessage(result));
                            $scope.currentGroup = null;
                            $scope.$broadcast("updateServerSelector");
                        });
                    }
                )
            },
            deleteTitle : "folder_delete"
        }
    };
    $scope.selectorData2 = {
        className   : className.groupPrograms,
        tableName   :   "folder_group_programs",
        loadMoreTitle : "folder_loadMore",
        filters      : [
            {
                title : "id",
                type : "number",
                field : "id"
            },
            {
                title : "name",
                type : "text",
                field : "title",
                default : true
            },{
                title : "section",
                type : "enum",
                field : "section.name",
                constants : [],
                default : true
            },{
                title : "folder_create_date",
                type : "date",
                field : "createDate"
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            },{
                name : "section.name",
                title : "section",
                translate : true,
                orderBy : true
            }, {
                name : "title",
                title : "name",
                orderBy : true
            },{
                name : "url",
                title : "folder_url"
            },{
                name : "createDate",
                title : "folder_create_date",
                orderBy : true,
                filter : "date"
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentGroup = item;
            $scope.currentClassName = className.groupPrograms;
            $scope.multiLanguageData.languages = {"ru":  item.descriptionRu, "en":  item.descriptionEn};
            updateCroppedImage();
        },
        actions : [
            {
                icon : "fa-plus",
                color : "#46BE28",
                tooltip : "folder_create",
                actionCallback : function (ev){
                    $scope.showDialog(ev,$scope,"createGroupPrograms.html",function(answer){
                        answer.sectionId = parseInt(answer.sectionId);
                        answer.type = "program";
                        applicationService.action($scope,"", className.folder,"createFolder",answer,function(result){
                            $scope.showToast($scope.getResultMessage(result));
                            $scope.$broadcast("updateServerSelector");
                        });
                    });
                }
            }
        ],
        deleteOptions : {
            deleteCallback : function (ev,item) {
                $scope.showConfirmDialog(
                    ev,
                    $scope.translate("folder_delete") + " " + item.title,
                    $scope.translate("folder_delete_confirmation"),
                    'Delete folder',
                    $scope.translate("delete"),
                    $scope.translate("cancel"),
                    function () {
                        applicationService.remove($scope,"",className.groupPrograms,$scope.currentGroup.id,function (result) {
                            $scope.showToast($scope.getResultMessage(result));
                            $scope.currentGroup = null;
                            $scope.$broadcast("updateServerSelector");
                        });
                    }
                )
            },
            deleteTitle : "folder_delete"
        }
    };
    $scope.selectorData3 = {
        className   : className.groupCourses,
        tableName   :   "folder_group_courses",
        loadMoreTitle : "folder_loadMore",
        filters      : [
            {
                title : "id",
                type : "number",
                field : "id"
            },
            {
                title : "name",
                type : "text",
                field : "title",
                default : true
            },{
                title : "section",
                type : "enum",
                field : "section.name",
                constants : [],
                default : true
            },{
                title : "folder_create_date",
                type : "dateTime",
                field : "createDate"
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            },{
                name : "section.name",
                title : "section",
                translate : true,
                orderBy : true
            }, {
                name : "title",
                title : "name",
                orderBy : true
            },{
                name : "url",
                title : "folder_url"
            },{
                name : "createDate",
                title : "folder_create_date",
                orderBy : true,
                filter : "dateTime"
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentGroup = item;
            $scope.currentClassName = className.groupCourses;
            $scope.multiLanguageData.languages = {"ru":  item.descriptionRu, "en":  item.descriptionEn};
            updateCroppedImage();
        },
        actions : [
            {
                icon : "fa-plus",
                color : "#46BE28",
                tooltip : "folder_create",
                actionCallback : function (ev){
                    $scope.showDialog(ev,$scope,"createGroupCourses.html",function(answer){
                        answer.sectionId = parseInt(answer.sectionId);
                        answer.type = "course";
                        applicationService.action($scope,"", className.folder,"createFolder",answer,function(result){
                            $scope.showToast($scope.getResultMessage(result));
                            $scope.$broadcast("updateServerSelector");
                        });
                    });
                }
            }
        ],
        deleteOptions : {
            deleteCallback : function (ev,item) {
                $scope.showConfirmDialog(
                    ev,
                    $scope.translate("folder_delete") + " " + item.title,
                    $scope.translate("folder_delete_confirmation"),
                    'Delete folder',
                    $scope.translate("delete"),
                    $scope.translate("cancel"),
                    function () {
                        applicationService.remove($scope,"",className.groupCourses,$scope.currentGroup.id,function (result) {
                            $scope.showToast($scope.getResultMessage(result));
                            $scope.currentGroup = null;
                            $scope.$broadcast("updateServerSelector");
                        });
                    }
                )
            },
            deleteTitle : "folder_delete"
        }
    };

    $scope.sections = [];
    applicationService.list($scope,"",className.section,function (section) {
        if (section.name == "programming" || section.name == "math" || section.name == "physic" || section.name == "design") {
            $scope.sections.push(section);
            $scope.selectorData1.filters[2].constants.push({
                key : section.name,
                value : section.name
            });
            $scope.selectorData2.filters[2].constants.push({
                key : section.name,
                value : section.name
            });
            $scope.selectorData3.filters[2].constants.push({
                key : section.name,
                value : section.name
            });
        }
    });

    $scope.multiLanguageData = {
        label : $scope.translate("folder_description")
    };

    $scope.updateGroup = function() {
        $scope.currentGroup.descriptionRu = $scope.multiLanguageData.languages.ru;
        $scope.currentGroup.descriptionEn = $scope.multiLanguageData.languages.en;
        applicationService.update($scope,"",$scope.currentClassName,$scope.currentGroup,function(result){
            $scope.showToast($scope.getResultMessage(result));
        });
    };

    $scope.croppedImg = {
        id : 'cover',
        save: function(file){
            updateImage(file);
        },
        areaType:"square",
        width : 270,
        height : 200
    };

    function updateCroppedImage(){
        $scope.croppedImg.src = applicationService.imageHref(className.folder,$scope.currentGroup.id);
        $scope.croppedImg.notUseDefault = $scope.currentGroup.imageViewExist;

        //Если изображение открывается первый раз событие не сработает так не зарегестрированно
        //Поэтому добавляется проверка для открытия
        $scope.croppedImg.setupImgae = true;

        $scope.$broadcast("updateCropImage"+$scope.croppedImg.id+"Event");
    }

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"cover",className.folder,"updateCover",{id:$scope.currentGroup.id},file,function (result) {
            $scope.showToast($scope.getResultMessage(result));
            if (result.status === "Complete") {
                $scope.currentGroup.imageViewExist = true;
                $scope.$broadcast("updateCropImage"+$scope.croppedImg.id+"Event");
            }
        });
    };
});