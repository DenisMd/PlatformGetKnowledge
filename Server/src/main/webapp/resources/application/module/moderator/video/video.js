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
            $scope.currentVideo = item;
            updateCroppedImage();
        },
        actions : [
            {
                icon : "fa-plus",
                color : "#46BE28",
                tooltip : "video_create",
                actionCallback : function (ev){
                    $scope.showDialog(ev,$scope,"createVideo.html",function(answer){
                        applicationService.create($scope,"", className.video,answer,function(result){
                            $scope.showToast($scope.getResultMessage(result));
                            $scope.$broadcast("updateServerSelector");
                        });
                    });
                }
            }
        ]
    };

    $scope.updateVideo = function () {
        applicationService.update($scope,"",className.video,$scope.currentVideo,function(result){
            $scope.showToast($scope.getResultMessage(result));
        });
    };

    $scope.croppedImg = {
        id : 'cover',
        save: function(file){
            updateImage(file);
        },
        areaType:"square",
        resultQuality : 1.0,
        resultSize : 500,
        isCrop : false
    };

    var updateCroppedImage  = function(){
        $scope.croppedImg.src = applicationService.imageHref(className.video, $scope.currentVideo.id);
        $scope.croppedImg.notUseDefault = $scope.currentVideo.imageViewExist;

        //Если изображение открывается первый раз событие не сработает так не зарегестрированно
        //Поэтому добавляется проверка для открытия
        $scope.croppedImg.setupImgae = true;

        $scope.$broadcast("updateCropImage"+$scope.croppedImg.id+"Event");
    };

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"image",className.video,"uploadCover",{videoId:$scope.currentVideo.id},file,function(result){
            $scope.showToast($scope.getResultMessage(result));
            if (result.status === "Complete") {
                $scope.currentVideo.imageViewExist = true;
                $scope.$broadcast("updateCropImage"+$scope.croppedImg.id+"Event");
            }
        });
    };

    $scope.uploadData = {
        btnTitle : "video_data",
        multiplyFiles : false,
        className : className.video,
        actionName : "uploadVideo",
        title : "video_data",
        parameters : {},
        maxFileSize : 512000,
        prepareParams : function (formData) {
            formData.data = JSON.stringify({videoId:$scope.currentVideo.id});
        }
    };
});