model.controller("simpleCommentController" , function($scope,$state,applicationService,className){

    $scope.commentErrorMessage = {};
    $scope.comments = [];

    var addComment = function (comment) {
        if (comment) {
            comment.sender.imageSrc = $scope.userImg(comment.sender.id);
            comment.sender.userUrl = $scope.createUrl("/user/" + comment.sender.id);
            $scope.comments.push(comment);
        }
    };

    $scope.filter = applicationService.createFilter($scope.getData().commentClassName,0,10);
    $scope.filter.createFiltersInfo();
    $scope.filter.setOrder("createTime",true);

    var doAction = function(){
        applicationService.filterRequest($scope,"commentsInfo", $scope.filter,addComment);
    };

    if (!$scope.getData().withoutEvent) {
        var eventName = $scope.getData().id ? "showSimpleComments" + $scope.getData().id : "showSimpleComments";
        $scope.$on(eventName, function () {
            $scope.comments = [];
            $scope.filter.equals($scope.getData().filedName, "number", $scope.getData().objectId);
            doAction();
        });
    } else {
        $scope.comments = [];
        $scope.filter.equals($scope.getData().filedName, "number", $scope.getData().objectId);
        doAction();
    }
    
    $scope.sendMessage = function(message) {
        var messageInfo = {
            objectId : $scope.getData().objectId,
            text : message
        };
        applicationService.action(null,"",$scope.getData().commentClassName,"addComment",messageInfo,function(result){
            $scope.commentErrorMessage = {};
            if (result.status === "Complete") {
                $scope.commentMessage = null;
                $scope.filter.result.first = 0;
                $scope.comments = [];
                doAction();
            } else {
                $scope.commentErrorMessage.message = $scope.getResultMessage(result);
                $scope.commentErrorMessage.type = 'danger';
            }
        });
    };

     function removeByIndex (index){
         $scope.comments.splice(index,1);
         $scope.commentsInfo.totalEntitiesCount--;
    }

    $scope.showDeleteCommentDialog = function(ev,videoCommentId,index) {
        $scope.showConfirmDialog(ev,
            $scope.translate("comment_remove"),
            "",
            'Delete comment',
            $scope.translate("delete"),
            null,
            function(){
                applicationService.action($scope,"",$scope.getData().commentClassName,"removeComment",{
                    "commentId" : videoCommentId
                },function (result) {
                    $scope.showToast($scope.getResultMessage(result));
                    if (result.status === 'Complete') {
                        removeByIndex(index);
                    }
                });
            }
        );
    };

     function blockByIdx(index,status) {
        $scope.comments[index].message = null;
         $scope.comments[index].commentStatus = status;
    }

    $scope.loadMore = function () {
        $scope.filter.increase(10);
        doAction();
    };
    
    $scope.blockStatuses = ["advertising","spam","insult"];
    $scope.showBlockCommentDialog = function(ev,videoCommentId,index) {
        $scope.showDialog(ev,$scope,"blockComment.html",function(answer){
            applicationService.action($scope,"",$scope.getData().commentClassName,"blockComment",{
                commentId : videoCommentId,
                status  : answer.capitalizeFirstLetter()
            },function(result){
                $scope.showToast($scope.getResultMessage(result));
                if (result.status === 'Complete') {
                    blockByIdx(index,answer.capitalizeFirstLetter());
                }
            });
        });
    };

    $scope.goToUserPage = function(comment) {
        if ($scope.getData().callbackBeforeGoToUserPage){
            $scope.getData().callbackBeforeGoToUserPage();
        }
        $scope.goTo(comment.sender.userUrl,true);
    };
});