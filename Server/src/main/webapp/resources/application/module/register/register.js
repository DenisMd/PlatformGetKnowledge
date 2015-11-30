model.controller("registerCtrl", function ($scope, $http,applicationService,className) {
    $scope.info = {
        "sex" : true
    };
    $scope.languageData = {
        "id" : "languages",
        "count" : 3,
        "filter":"title",
        "class" : "input-group-lg",
        "listName" : "lang",
        "selectValue": null,
        "callback" : function (value){
            $scope.info.language = value.name;
        }
    };

    applicationService.list($scope,"lang",className.language, function (item) {
        item.title = $scope.translate(item.name.toLowerCase())
    });
    //applicationService.action($scope, "lang", className.country, "getCountries", {language : 'Ru'}, function (item) {
    //    item.title = $scope.translate(item.countryName);
    //});

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
