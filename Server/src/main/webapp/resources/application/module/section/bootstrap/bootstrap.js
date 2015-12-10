model.controller("bootstrapCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    $scope.serviceTable = {
        title : "bootstrap_title",
        columns : ["id", "name", "bootstrapState", "order", "repeat"],
        content : [],
        tabs : [{
            title : "service",
            columns : [{name : "id" , "type" : "number", disabled : true} , {name : "name" , "type" : "string", disabled : true},{name : "bootstrapState" , "type" : "string", disabled : true},{name : "errorMessage" , "type" : "string" , disabled : true},{name : "stackTrace" , "type" : "textarea" , disabled : true}],
            readOnly : true
        } , {
            title : "doBootstrap",
            actionName : "do",
            className : className.bootstrap_services,
            columns : [{name : "domain" , type : "text"} ,{name : "email" , type : "text"},{name : "password" , type : "text"},{name : "firstName" , type : "text"},{name : "lastName" , type : "text"},{name : "initPassword" , type : "text"}],
            buttonText  : "doBootstrap"

        }]
    };
    applicationService.list($scope , "services",className.bootstrap_services , function(bootstrapService){
        $scope.serviceTable.content.push(bootstrapService);
    });
});