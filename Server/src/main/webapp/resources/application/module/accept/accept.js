model.controller("acceptCtrl", function ($scope, $state,$http,className,applicationService,pageService) {
    var uuid = pageService.getPathVariable("accept",$state.params.path);
    $scope.uuid = uuid;
    applicationService.action($scope , "result" , className.system_event , "completeRegistration" , {
        "uuid" : uuid
    });
});