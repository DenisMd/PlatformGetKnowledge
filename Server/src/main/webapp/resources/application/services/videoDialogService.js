model.factory('videoDialogService', function(applicationService,className) {
    var videoInfo = {};
    var mainScope;
    var callbackFunction = function (videoComment) {
        videoComment.sender.imageSrc = mainScope.userImg(videoComment.sender.id);
        mainScope.videoComments.push(videoComment);
    };
    return {
        init : function($scope) {
            mainScope = $scope;
            mainScope.videoComments = [];
        },

        afterOpen : function(videoId) {
            mainScope.videoComments = [];
            videoInfo = {
                videoId : videoId,
                first : 0,
                max : 10
            };
            mainScope.videoCommentsCount = videoInfo.max;
            applicationService.action(mainScope,"totalVideoCommentsCount",className.video,"countComments",videoInfo,function(){
                applicationService.action(mainScope,"",className.video,"getComments",videoInfo,callbackFunction);
            });
        },

        loadMore : function(number){
            //Больше нету кооментариев
            if (mainScope.totalVideoCommentsCount <= videoInfo.first) {
                return;
            }
            mainScope.videoCommentsCount += number;
            videoInfo.first += number;
            applicationService.action(mainScope,"",className.video,"getComments",videoInfo,callbackFunction);
        },

        getVideoInfo : function(){
            return videoInfo;
        },

        removeByIndex : function(index){
            mainScope.videoComments.splice(index,1);
            mainScope.totalVideoCommentsCount--;
        },

        blockByIndx : function(index,status) {
            mainScope.videoComments[index].message = null;
            mainScope.videoComments[index].commentStatus = status;
        }
    }
});