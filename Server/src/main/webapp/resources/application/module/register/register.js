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
        "required" : true,
        "callback" : function (value){
            $scope.info.language = value.name;
        }
    };

    applicationService.list($scope,"lang",className.language, function (item) {
        item.title = $scope.translate(item.name.toLowerCase())
    });

    $scope.password = "";
    $scope.signUp = function() {
        if ($scope.registerForm.$invalid) return;
        applicationService.action($scope,"registerInfo" , className.userInfo , "register", $scope.info, function(registerInfo) {
            $scope.error = false;
            if (registerInfo != 'Complete') {
                $scope.error = true;
            }
        });
    };

    $scope.getClass = function(){
        var cssClass = "";
        if ($scope.error){
            cssClass = "alert-danger";
        } else if ($scope.registerInfo == 'Complete'){
            cssClass = "alert-info";
        } else {
            cssClass = "div-hidden";
        }
        return cssClass;
    };
});
