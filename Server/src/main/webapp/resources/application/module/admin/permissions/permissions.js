model.controller("permissionsCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    $scope.bootstrapSelector = {
        title : "permissions_title",
        columns : ["id", "permissionName", "note"],
        content : [],
        tabs : [{
            title : "permission",
            columns : [{name : "id" , "type" : "number"} , {name : "permissionName" , "type" : "string"},{name : "note" , "type" : "string"}],
            buttonText  : "updatePermission",
            actionName : "update",
            className : className.permissions
        }]
    };

    $scope.panelData = {
        items : [{
            title : "createPermission",
            actionName : "create",
            className : className.permissions,
            columns : [{name : "permissionName" , type : "text"} ,{name : "note" , type : "text"}],
            buttonText  : "create",
            label : "fa-plus",
            style : {color : '#3CB13B'}
        }]
    };

    applicationService.list($scope , "services",className.permissions , function(permission){
        $scope.bootstrapSelector.content.push(permission);
    });
});