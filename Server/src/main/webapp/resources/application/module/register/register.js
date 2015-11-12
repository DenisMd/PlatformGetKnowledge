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
        applicationService.action($scope,"registerInfo" , className.userInfo , "register", $scope.info);
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