model.controller("registerCtrl", function ($scope, $http,applicationService) {
    applicationService.pageInfo($scope);

    $scope.info = {};
    $scope.password = "";
    $scope.register = function(login , password , firstName , lastName) {
        applicationService.action($scope,"registerInfo" , "com.getknowledge.modules.userInfo.UserInfo" , "register",
            {
                "login" : login,
                "password" : password,
                "firstName" : firstName,
                "lastName" : lastName
            });
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