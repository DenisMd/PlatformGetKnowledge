model.controller("forgotPasswordCtrl", function ($scope,applicationService,className) {
    $scope.forgotPassword = function (email) {
        applicationService.action($scope, "resultForgotPassword", className.userInfo,"forgotPassword" , {"email" : email});
    };
});