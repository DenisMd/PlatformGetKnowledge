model.controller("acceptCtrl", function ($scope, $state,$http,applicationService) {
    applicationService.pageInfo($scope.$parent);
    var classNameRegisterInfo = "com.getknowledge.modules.userInfo.registerInfo.RegisterInfo";
    var uuid = applicationService.getPathVariable("accept");
    applicationService.action($scope , "result" , classNameRegisterInfo , "completeRegistration" , {
        "uuid" : uuid
    });
});