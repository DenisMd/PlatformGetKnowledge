model.controller("registerCtrl", function ($scope, $http,applicationService) {
    applicationService.pageInfo($scope);
    $scope.register = function(login , password , firstName , lastName) {
        applicationService.action($scope,"registerResult" , "com.getknowledge.modules.userInfo.UserInfo" , "register",
            {
                "login" : login,
                "password" : password,
                "firstName" : firstName,
                "lastName" : lastName
            });
    }
});