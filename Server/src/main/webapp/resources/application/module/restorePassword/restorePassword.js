model.controller("restorePasswordCtrl", function ($scope,$state,applicationService,className,pageService) {

    var uuid = pageService.getPathVariable("restorePassword",$state.params.path);
    $scope.restorePassword = function (password) {
        applicationService.action($scope,"",className.system_event,"restorePassword",{
            "uuid" : uuid,
            "password" : password
        },function(result){
            if (result.status == 'Complete'){
                $scope.restorePasswordInfo = {
                    message : $scope.translate("restore_password_complete"),
                    type : 'success'
                };
            } else {
                $scope.restorePasswordInfo = {
                    message : $scope.translate("restore_password_failed"),
                    type : 'danger'
                };
            }
        });
    };

});