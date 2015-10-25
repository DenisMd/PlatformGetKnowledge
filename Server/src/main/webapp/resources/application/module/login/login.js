model.controller("loginCtrl", function ($scope, $http,applicationService) {
    applicationService.pageInfo($scope);
    $scope.login = function(login , password) {
        applicationService.login(login,password);
    }
});