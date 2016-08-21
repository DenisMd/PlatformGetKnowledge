model.factory('videoDialogService', function(applicationService,className) {
    var mainScope;
    return {
        init : function($scope) {
            mainScope = $scope;
        },

        afterOpen : function(video, showComments) {
            mainScope.commentData.objectId = video.id;
            mainScope.video = video;
            mainScope.showComments = angular.isUndefined(showComments)? true : showComments;
            mainScope.$broadcast("showSimpleComments");
        }
    }
});