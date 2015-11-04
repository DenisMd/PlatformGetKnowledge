model.controller("loginCtrl", function ($scope, $state,$http,applicationService) {
    applicationService.pageInfo($scope);
    $scope.login = function(login , password) {
        applicationService.login($scope,"loginResult",login,password,function(data){
            console.log(data);
            if (data.message == 'success') {
                console.log("action");
                $state.go($state.current, {}, {reload: true});
            }
        });
    }
});