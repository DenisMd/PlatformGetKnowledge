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
                $scope.commentMessage = null;
                videoDialogService.afterOpen(videoInfo.videoId);
            } else {
                $scope.videoCommentErrorMessage.message = $scope.getResultMessage(result);
                $scope.videoCommentErrorMessage.type = 'danger';
            }
        });
    };


    $scope.showDeleteVideoDialog = function(ev,videoCommentId,index) {
        $scope.showConfirmDialog(ev,
                                $scope.translate("video_comments_remove"),
                                'Delete video comment',
                                $scope.translate("delete"),
                                null,
            function(){
                applicationService.action($scope,"",className.videoComments,"removeVideoComment",{
                    "videoCommentId" : videoCommentId
                },function (result) {
                    $scope.showToast($scope.getResultMessage(result));
                    if (result.status === 'Complete') {
                        videoDialogService.removeByIndex(index);
                    }
                });
            }
        );
    };

    $scope.blockStatuses = ["advertising","spam","insult"];
    $scope.showBlockCommentDialog = function(ev,videoCommentId,index) {
        $scope.showDialog(ev,$scope,"blockVideoComment.html",function(answer){
            console.log(answer);
            applicationService.action($scope,"",className.videoComments,"blockComment",{
                videoCommentId : videoCommentId,
                status  : answer.capitalizeFirstLetter()
            },function(result){
                $scope.showToast($scope.getResultMessage(result));
                if (result.status === 'Complete') {
                    videoDialogService.blockByIndx(index,answer.capitalizeFirstLetter());
                }
            });
        });
    };



});