model.controller("userCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    var userId = pageService.getPathVariable("user",$state.params.path);
    if (userId) {
        applicationService.read($scope, "user_info" , className.userInfo, userId);
    }
});