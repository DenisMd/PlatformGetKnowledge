model.controller("loginCtrl", function ($scope, $state,$http,applicationService) {
    $scope.info = {};
    $scope.login = function() {
        applicationService.login($scope,"loginResult",$scope.info.login,$scope.info.password,function(data){
            if (data.message === 'success') {
                applicationService.action($scope.$parent, "user", "com.getknowledge.modules.userInfo.UserInfo", "getAuthorizedUser", {});
            } else {
                $scope.errorMsg = data;
            }
        });
    }
});