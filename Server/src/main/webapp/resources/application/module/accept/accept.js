model.controller("acceptCtrl", function ($scope,$state,className,applicationService,pageService) {

    $scope.uuid = pageService.getPathVariable("accept",$state.params.path);

    applicationService.action($scope , "" , className.system_event , "completeRegistration" , {
        "uuid" : $scope.uuid
    }, function(result){
        $scope.result = "";
        if (result == 'Complete') {
            $scope.result = 'complete';
        } else if (result == 'NotFound') {
            $scope.result = 'not_found';
        } else if (result == 'AlreadyActivate') {
            $scope.result = 'already_activate';
        }
    });
});