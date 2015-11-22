model.controller("registerCtrl", function ($scope, $http,applicationService,className) {
    $scope.info = {
        "sex" : true
    };
    $scope.languageData = {
        "id" : "languages",
        "count" : 3,
        "list" : []
    };
    applicationService.list($scope,"langs",className.language, function (item) {
        $scope.languageData.list.push($scope.translate(item.name.toLowerCase()));
    });

    $scope.password = "";
    $scope.signUp = function() {
        if ($scope.registerForm.$invalid) return;
        $scope.info.language = 'En';
        applicationService.action($scope,"registerInfo" , className.userInfo , "register", $scope.info, function(registerInfo) {
            $scope.error = false;
            if (registerInfo != 'Complete') {
                $scope.error = true;
            }
        });
    }
});
