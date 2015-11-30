model.controller("acceptCtrl", function ($scope, $state,$http,className,applicationService,pageService) {
    var uuid = pageService.getPathVariable("accept",$state.params.path);
    applicationService.action($scope , "result" , className.registerInfo , "completeRegistration" , {
        "uuid" : uuid
    });
});