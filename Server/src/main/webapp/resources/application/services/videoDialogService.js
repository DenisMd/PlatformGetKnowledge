model.factory('videoDialogService', function(applicationService,className) {
    var mainScope;
    return {
        init : function($scope) {
            mainScope = $scope;
        },

        afterOpen : function(videoId) {
            mainScope.commentData.objectId = videoId;
            mainScope.$broadcast("showSimpleComments");
        }
    }
});