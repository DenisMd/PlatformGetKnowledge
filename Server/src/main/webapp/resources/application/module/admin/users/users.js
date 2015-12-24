model.controller("usersCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    $scope.usersSelector = {
        title : "user_title",
        columns : ["id", "login", "createDate"],
        content : [],
        tabs : [{
            title : "user",
            columns : [{name : "login" , "type" : "string"},{name : "createDate" , "type" : "string"}],
            readOnly : true
        }]
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

    applicationService.list($scope , "services",className.users , function(permission){
        $scope.usersSelector.content.push(permission);
    });
});