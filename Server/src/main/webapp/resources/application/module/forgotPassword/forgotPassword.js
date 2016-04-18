model.controller("forgotPasswordCtrl", function ($scope,applicationService,className) {
    $scope.forgotPassword = function (email) {
        applicationService.action($scope, "", className.userInfo,"forgotPassword" , {"email" : email},function(result){
            if (result.status === 'Complete') {
                $scope.forgotError = {
                    message : $scope.translate("forgot_complete"),
                    type : 'success'
                };
            } else if (result.status === 'Failed'){
                $scope.forgotError = {
                    message : $scope.translate("forgot_email_not_found"),
                    type : 'danger'
                };
            } else {
                $scope.forgotError = {
                    message : $scope.translate("forgot_email_not_send"),
                    type : 'danger'
                };
            }

        });
    };
});