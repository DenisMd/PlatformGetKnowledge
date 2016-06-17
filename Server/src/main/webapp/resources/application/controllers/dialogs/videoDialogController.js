model.controller("videoDialogController",function($scope,videoDialogService,applicationService,className){
    
    $scope.commentData = {
        commentClassName : className.videoComments,
        filedName : "video.id",
        callbackBeforeGoToUserPage : function() {
            $('#videoModal').modal('hide');
        }
    };
    
    videoDialogService.init($scope);
});