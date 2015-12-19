model.controller("forgotPasswordCtrl", function ($scope,applicationService,className) {
    $scope.forgetPassword = function (email) {
        applicationService.action($scope, "resultForgetPassword", className.userInfo,"forgotPassword" , {"email" : email});
    }
});