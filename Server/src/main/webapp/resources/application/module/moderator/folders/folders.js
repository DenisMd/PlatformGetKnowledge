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
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            }, {
                name : "title",
                title : "name",
                orderBy : true
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentGroup = item;
            $scope.currentClassName = className.groupBooks;
            $scope.multiLanguageData.languages = {"ru":  item.descriptionRu, "en":  item.descriptionEn};
        },
        actions : [
            {
                icon : "fa-plus",
                color : "#46BE28",
                tooltip : "folder_create",
                actionCallback : function (ev){
                    $scope.showDialog(ev,$scope,"createGroupBooks.html",function(answer){
                        answer.sectionId = parseInt(answer.sectionId);
                        applicationService.action($scope,"", className.groupBooks,"createGroupBooks",answer,function(result){
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
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            }, {
                name : "title",
                title : "name",
                orderBy : true
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentGroup = item;
            $scope.currentClassName = className.groupPrograms;
            $scope.multiLanguageData.languages = {"ru":  item.descriptionRu, "en":  item.descriptionEn};
        },
        actions : [
            {
                icon : "fa-plus",
                color : "#46BE28",
                tooltip : "folder_create",
                actionCallback : function (ev){
                    $scope.showDialog(ev,$scope,"createGroupPrograms.html",function(answer){
                        answer.sectionId = parseInt(answer.sectionId);
                        applicationService.action($scope,"", className.groupPrograms,"createGroupPrograms",answer,function(result){
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
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            }, {
                name : "title",
                title : "name",
                orderBy : true
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentGroup = item;
            $scope.currentClassName = className.groupCourses;
            $scope.multiLanguageData.languages = {"ru":  item.descriptionRu, "en":  item.descriptionEn};
        },
        actions : [
            {
                icon : "fa-plus",
                color : "#46BE28",
                tooltip : "folder_create",
                actionCallback : function (ev){
                    $scope.showDialog(ev,$scope,"createGroupCourses.html",function(answer){
                        answer.sectionId = parseInt(answer.sectionId);
                        applicationService.action($scope,"", className.groupCourses,"createGroupCourses",answer,function(result){
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

    applicationService.list($scope,"sections",className.section);

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

    var croppedImg = {
        save: function(file){
            updateImage(file);
        },
        areaType:"square"
    };

    $scope.getCropImageData  = function(){
        croppedImg.src = applicationService.imageHref($scope.currentClassName,$scope.currentGroup.id);
        croppedImg.notUseDefault = $scope.currentGroup.imageViewExist;
        return croppedImg;
    };

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"cover",$scope.currentClassName,"updateCover",{id:$scope.currentGroup.id},file,function (result) {
            $scope.showToast($scope.getResultMessage(result));
            if (result.status === "Complete") {
                $scope.currentGroup.imageViewExist = true;    
            }
        });
    };
});