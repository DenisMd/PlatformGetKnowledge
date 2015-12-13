model.controller("rolesCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    $scope.rolesSelector = {
        title : "roles_title",
        columns : ["id", "roleName", "note"],
        content : [],
        tabs : [{
            title : "role",
            columns : [{name : "roleName" , "type" : "string"},{name : "note" , "type" : "string"}],
            buttonText  : "updateRole",
            actionName : "update",
            className : className.roles
        }]
    };

    $scope.panelData = {
        items : [{
            title : "createRole",
            actionName : "create",
            className : className.roles,
            columns : [{name : "roleName" , type : "text"} ,{name : "note" , type : "text"}],
            buttonText  : "create",
            label : "fa-plus",
            style : {color : '#3CB13B'}
        }]
    };

    applicationService.list($scope , "services",className.roles , function(permission){
        $scope.rolesSelector.content.push(permission);
    });
});