model.controller("registerCtrl", function ($scope, $http,applicationService) {
    applicationService.pageInfo($scope);

    $scope.info = {
        "sex" : true
    };
    $scope.password = "";
    $scope.signUp = function() {
        if (registerForm.$invalid) return;
        applicationService.action($scope,"registerInfo" , "com.getknowledge.modules.userInfo.UserInfo" , "register", $scope.info);
    }
});


model.directive("useValidation", function () {
    return {
        restrict: 'A',
        require:'ngModel',
        scope:{
            type:"@useValidation",
            options: "=options"
        },
        link:function (scope, elm, attrs,ngModel) {
           if (!scope.type) return;
            switch (scope.type){

                case "compareTo":
                    ngModel.$validators.compareTo = function(value){
                        return scope.options.value === value;
                    };
                    scope.$watch("options", function() {
                        ngModel.$validate();
                    });
                    break;

                default : return;
            }
        }
    };
});