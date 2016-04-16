model.controller("videoDialogController",function($scope,videoDialogService){
    videoDialogService.init($scope);

    $scope.loadMoreComments = function(){
        videoDialogService.loadMore(10);
    }
});