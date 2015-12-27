model.controller("bootstrapCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    $scope.bootstrapSelector = {
        title : "bootstrap_title",
        columns : ["id", "name", "bootstrapState", "order", "repeat"],
        content : [],
        callback : function (item) {
            $scope.editorData.item = item;
        }
    };

    $scope.editorData = {
        item : null,
        tabs : [{
            title : "service",
            columns : [{name : "id" , "type" : "number", disabled : true} , {name : "name" , "type" : "string", disabled : true},{name : "bootstrapState" , "type" : "string", disabled : true}, {name : "repeat" , type : "boolean"}, {name : "errorMessage" , "type" : "string" , disabled : true},{name : "stackTrace" , "modal" : "inputs/textPlain" , disabled : true}],
            readOnly : false,
            className : className.bootstrap_services,
            actionName : "update",
            buttonText  : "update"
        }]
    };

    $scope.panelData = {
        items : [{
            title : "doBootstrap",
            actionName : "do",
            className : className.bootstrap_services,
            columns : [{name : "domain" , type : "text"} ,{name : "email" , type : "text"},{name : "password" , type : "text"},{name : "firstName" , type : "text"},{name : "lastName" , type : "text"},{name : "initPassword" , type : "text"}],
            buttonText  : "doBootstrap",
            label : "fa-cog fa-spin",
            style : {color : '#5E5DD6'}
        }]
    };

    applicationService.list($scope , "services",className.bootstrap_services , function(bootstrapService){
        $scope.bootstrapSelector.content.push(bootstrapService);
    });
});