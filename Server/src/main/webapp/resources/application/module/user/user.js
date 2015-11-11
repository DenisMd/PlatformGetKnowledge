model.controller("userCtrl", function ($scope, $state,$http,applicationService) {

    var userId = applicationService.getPathVariable("user");
    if (userId) {
        $scope.read($scope, "user_info" , className.userInfo, userId);
    }
});