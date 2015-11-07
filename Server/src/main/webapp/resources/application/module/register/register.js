model.controller("registerCtrl", function ($scope, $http,applicationService,validationService) {
    applicationService.pageInfo($scope);
    $scope.compareTo = validationService.compareTo;

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

model.service("validationService", function(){
    this.compareTo = function (value,anotherInput){
        console.log(anotherInput);
        return angular.equals(anotherInput,value);
    }
});

model.directive("useValidation", function () {
    return {
        restrict: 'A',
        require:'ngModel',
        scope:{
            validationFunction:"&useValidation",
            options: "@options"
        },
        link:function (scope, elm, attrs, ngModelCtrl) {
            console.log(scope.options);
            ngModelCtrl.$parsers.unshift(function (viewValue) {
                ngModelCtrl.$setValidity('data', scope.validationFunction(viewValue,scope.options));
                return viewValue;
            });

            ngModelCtrl.$formatters.unshift(function (modelValue) {
                ngModelCtrl.$setValidity('data', scope.validationFunction(modelValue,scope.options));
                return modelValue;
            });
        }
    };
});