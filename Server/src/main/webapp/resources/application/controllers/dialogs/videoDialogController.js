model.controller("videoDialogController",function($scope,$mdDialog,videoDialogService,applicationService,className){
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

    $scope.showDeleteVideoDialog = function(ev,videoCommentId,index) {
        var confirm = $mdDialog.confirm()
            .title($scope.translate("video_comments_remove"))
            .textContent()
            .targetEvent(ev)
            .ariaLabel('Delete video comment')
            .ok($scope.translate("delete"))
            .cancel($scope.translate("cancel"));
        $mdDialog.show(confirm).then(function() {
            applicationService.action($scope,"",className.video,"removeVideoComment",{
                "videoCommentId" : videoCommentId
            },function (result) {
                $scope.showToast($scope.getResultMessage(result));
                if (result.status === 'Complete') {
                    videoDialogService.removeByIndex(index);
                }
            });
        });
    };
});