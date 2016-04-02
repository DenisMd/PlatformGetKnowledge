model.controller("restorePasswordCtrl", function ($scope,$state,applicationService,className,pageService) {

    $scope.uuid = pageService.getPathVariable("restorePassword",$state.params.path);
    $scope.restorePassword = function (password) {
        applicationService.action($scope,"resultRestorePassword",className.system_event,"restorePassword",{
            "uuid" : $scope.uuid,
            "password" : password
        });
    };

});