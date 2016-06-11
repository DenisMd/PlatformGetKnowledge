model.controller("simpleCommentController" , function($scope,$state,applicationService,className){

    $scope.commentErrorMessage = {};
    $scope.comments = [];

    var addComment = function (comment) {
        comment.sender.imageSrc = $scope.userImg(comment.sender.id);
        comments.push(comment);
    };

    $scope.filter = applicationService.createFilter($scope.getData().commentClassName,0,10);
    $scope.filter.createFiltersInfo();

    var doAction = function(){
        applicationService.filterRequest($scope,"commentsInfo", $scope.filter,addComment);
    };

    $scope.$on("showSimpleComments" , function () {
        $scope.filter.equals($scope.getData().filedName,"number",$scope.getData().objectId);
        doAction();
    });


    //
    // loadMore : function(number){
    //     //Больше нету кооментариев
    //     if (mainScope.totalVideoCommentsCount <= videoInfo.first) {
    //         return;
    //     }
    //     mainScope.videoCommentsCount += number;
    //     videoInfo.first += number;
    //     applicationService.action(mainScope,"",className.video,"getComments",videoInfo,callbackFunction);
    // };
    //
    // getVideoInfo : function(){
    //     return videoInfo;
    // };
    //
    // removeByIndex : function(index){
    //     mainScope.videoComments.splice(index,1);
    //     mainScope.totalVideoCommentsCount--;
    // };
    //
    // blockByIndx : function(index,status) {
    //     mainScope.videoComments[index].message = null;
    //     mainScope.videoComments[index].commentStatus = status;
    // }
    //
    //
    // $scope.loadMoreComments = function(){
    //     videoDialogService.loadMore(10);
    // };
    //
    $scope.sendMessage = function(message) {
        var messageInfo = {
            objectId : $scope.getData().objectId,
            text : message
        };
        applicationService.action(null,"",$scope.getData().className,"addComment",messageInfo,function(result){
            $scope.commentErrorMessage = {};
            if (result.status === "Complete") {
                $scope.commentMessage = null;
                //TODO : перечитать
            } else {
                $scope.commentErrorMessage.message = $scope.getResultMessage(result);
                $scope.commentErrorMessage.type = 'danger';
            }
        });
    };
    //
    //
    $scope.showDeleteCommentDialog = function(ev,videoCommentId,index) {
        $scope.showConfirmDialog(ev,
            $scope.translate("video_comments_remove"),
            "",
            'Delete comment',
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
    //
    // $scope.blockStatuses = ["advertising","spam","insult"];
    // $scope.showBlockCommentDialog = function(ev,videoCommentId,index) {
    //     $scope.showDialog(ev,$scope,"blockVideoComment.html",function(answer){
    //         console.log(answer);
    //         applicationService.action($scope,"",className.videoComments,"blockComment",{
    //             videoCommentId : videoCommentId,
    //             status  : answer.capitalizeFirstLetter()
    //         },function(result){
    //             $scope.showToast($scope.getResultMessage(result));
    //             if (result.status === 'Complete') {
    //                 videoDialogService.blockByIndx(index,answer.capitalizeFirstLetter());
    //             }
    //         });
    //     });
    // };
});