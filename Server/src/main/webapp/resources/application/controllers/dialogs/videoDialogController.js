model.controller("videoDialogController",function($scope,videoDialogService,applicationService,className){
    videoDialogService.init($scope);

    $scope.videoCommentErrorMessage = {};

    $scope.loadMoreComments = function(){
        videoDialogService.loadMore(10);
    };

    $scope.sendMessage = function(message) {
        var videoInfo = {
            videoId : videoDialogService.getVideoInfo().videoId,
            text : message
        };
        applicationService.action(null,"",className.video,"addComment",videoInfo,function(result){
            $scope.videoCommentErrorMessage = {};
            if (result.status === "Complete") {
                videoDialogService.afterOpen(videoInfo.videoId);
            } else {
                $scope.videoCommentErrorMessage.message = $scope.getResultMessage(result);
                $scope.videoCommentErrorMessage.type = 'danger'
            }
        });
    };
});