model.controller("registerCtrl", function ($scope, $http,applicationService,className) {
    $scope.info = {
        "sex" : true
    };
    applicationService.list($scope,"langs",className.language, function (item) {
        item.name = item.name.toLowerCase();
        console.log(item);
    });

    $scope.password = "";
    $scope.signUp = function() {
        if (registerForm.$invalid) return;
        $scope.info.language = 'En';
        applicationService.action($scope,"registerInfo" , className.userInfo , "register", $scope.info, function(registerInfo) {
            $scope.error = false;
            if (registerInfo != 'Complete') {
                $scope.error = true;
            }
        });
    }
});
