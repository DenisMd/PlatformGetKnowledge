model.controller("videoDialogController",function($scope,videoDialogService,applicationService,className){
    
    $scope.commentData = {
        commentClassName : className.videoComments,
        filedName : "video.id"
    };
    
    videoDialogService.init($scope);
});