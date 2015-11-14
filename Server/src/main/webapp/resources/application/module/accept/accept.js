model.controller("acceptCtrl", function ($scope, $state,$http,className,applicationService,pageService,cfpLoadingBar) {
    var uuid = pageService.getPathVariable("accept");
    applicationService.action($scope , "result" , className.registerInfo , "completeRegistration" , {
        "uuid" : uuid
    },function(){
        cfpLoadingBar.start();
    });
});