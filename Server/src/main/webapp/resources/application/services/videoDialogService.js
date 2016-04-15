model.factory('videoDialogService', function(applicationService,className) {
    var videoInfo = {};
    var mainScope;
    var callbackFunction = function (videoComment) {
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
            applicationService.action(mainScope,"",className.video,"getComments",videoInfo,callbackFunction);
        },

        loadMore : function(number){
            videoInfo.first += number;
            applicationService.action(mainScope,"",className.video,"getComments",videoInfo,callbackFunction);
        }
    }
});