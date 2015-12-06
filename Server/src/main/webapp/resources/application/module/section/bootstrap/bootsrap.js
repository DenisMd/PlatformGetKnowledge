model.controller("bootstrapCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    $scope.serviceTable = {
        title : "bootstrap_title",
        columns : ["id", "name", "bootstrapState", "repeat"],
        content : []
    };
    applicationService.list($scope , "services",className.bootstrap_services , function(bootstrapService){
        $scope.serviceTable.content.push(bootstrapService);
    });
});