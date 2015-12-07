model.controller("bootstrapCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    $scope.serviceTable = {
        title : "bootstrap_title",
        columns : ["id", "name", "bootstrapState", "order", "repeat"],
        content : [],
        tabs : [{
            title : "Service",
            columns : [{name : "id" , "type" : "number"} , {name : "name" , "type" : "string"},{name : "bootstrapState" , "type" : "string"},{name : "errorMessage" , "type" : "string"},{name : "stackTrace" , "type" : "textarea"}],
            readOnly : true
        } , {
            title : "doBootstrap",
            actionName : "do",
            className : className.bootstrap_services,
            columns : [{name : "domain" , type : "text"} ,{name : "email" , type : "text"},{name : "password" , type : "text"},{name : "firstName" , type : "text"},{name : "lastName" , type : "text"} ],
            buttonText  : "doBootstrap"

        }]
    };
    applicationService.list($scope , "services",className.bootstrap_services , function(bootstrapService){
        $scope.serviceTable.content.push(bootstrapService);
    });
});