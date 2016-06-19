model.factory('videoDialogService', function(applicationService,className) {
    var mainScope;
    return {
        init : function($scope) {
            mainScope = $scope;
        },

        afterOpen : function(video) {
            mainScope.commentData.objectId = video.id;
            mainScope.video = video;
            mainScope.$broadcast("showSimpleComments");
        }
    }
});