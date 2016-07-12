model.controller("videoCtrl", function ($scope, $state,$http,applicationService,pageService,className) {

    $scope.selectorData = {
        className   : className.video,
        tableName   :   "video",
        loadMoreTitle : "video_load_more",
        filters      : [
            {
                title : "id",
                type : "number",
                field : "id"
            },
            {
                title : "name",
                type : "text",
                field : "videoName",
                default : true
            },{
                title : "video_link",
                type : "text",
                field : "link",
                default : true
            },{
                title: "video_upload_time",
                type : "dateTime",
                field : "uploadTime"
            },{
                title : "video_allow",
                type : "check_box",
                field : "allowEveryOne",
                default : true
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            },{
                name : "videoName",
                title : "name",
                orderBy : true
            },{
                name :  "link",
                title : "video_link"
            },{
                name : "allowEveryOne",
                title : "video_allow"
            },{
                name : "uploadTime",
                title: "video_upload_time",
                filter : "date",
                orderBy : true
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentKnowledge = item;
            updateCroppedImage();
        },
        actions : [
            {
                icon : "fa-plus",
                color : "#46BE28",
                tooltip : "knowledge_create",
                actionCallback : function (ev){
                    $scope.showDialog(ev,$scope,"createKnowledge.html",function(answer){
                        applicationService.create($scope,"", className.knowledge,answer,function(result){
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
                    $scope.translate("knowledge_delete") + " " + item.name,
                    $scope.translate("knowledge_delete_confirmation"),
                    'Delete knowledge',
                    $scope.translate("delete"),
                    $scope.translate("cancel"),
                    function () {
                        applicationService.remove($scope,"",className.knowledge,$scope.currentKnowledge.id,function (result) {
                            $scope.showToast($scope.getResultMessage(result));
                            $scope.currentKnowledge = null;
                            $scope.$broadcast("updateServerSelector");
                        });
                    }
                )
            },
            deleteTitle : "knowledge_delete"
        }
    };

    $scope.knowledgeType = ['Programming' , 'Design' , 'Math' , 'Physic'];

    $scope.updateKnowledge = function () {
        applicationService.update($scope,"",className.knowledge,$scope.currentKnowledge,function(result){
            $scope.showToast($scope.getResultMessage(result));
        });
    };

    $scope.croppedImg = {
        id : 'cover',
        save: function(file){
            updateImage(file);
        },
        areaType:"circle",
        resultQuality : 1.0,
        resultSize : 500,
        isCrop : true
    };

    var updateCroppedImage  = function(){
        $scope.croppedImg.src = applicationService.imageHref(className.knowledge, $scope.currentKnowledge.id);
        $scope.croppedImg.notUseDefault = $scope.currentKnowledge.imageViewExist;

        //Если изображение открывается первый раз событие не сработает так не зарегестрированно
        //Поэтому добавляется проверка для открытия
        $scope.croppedImg.setupImgae = true;

        $scope.$broadcast("updateCropImage"+$scope.croppedImg.id+"Event");
    };

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"image",className.knowledge,"uploadImage",{knowledgeId:$scope.currentKnowledge.id},file,function(result){
            $scope.showToast($scope.getResultMessage(result));
            if (result.status === "Complete") {
                $scope.currentKnowledge.imageViewExist = true;
                $scope.$broadcast("updateCropImage"+$scope.croppedImg.id+"Event");
            }
        });
    };
});