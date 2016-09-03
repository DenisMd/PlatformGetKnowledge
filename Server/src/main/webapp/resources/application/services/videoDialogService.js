model.factory('videoDialogService', function(applicationService,className, $timeout) {
    var mainScope;
    return {
        init : function($scope) {
            mainScope = $scope;
        },

        afterOpen : function(video, showComments) {
            mainScope.commentData.objectId = video.id;
            mainScope.video = video;
            mainScope.showComments = angular.isUndefined(showComments)? true : showComments;
            $timeout(function(){mainScope.$broadcast("showSimpleComments")}, 500);
        }
    }
});