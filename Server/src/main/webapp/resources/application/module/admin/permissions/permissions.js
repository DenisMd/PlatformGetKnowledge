model.controller("permissionsCtrl", function ($scope,$timeout, applicationService, className) {//Данные для статического селектара

    function permissionList() {
        $scope.selectorData.list = [];
        applicationService.list($scope , "permissions", className.permissions,function (item) {
            $scope.selectorData.list.push(item);
        });
    }

    $scope.selectorData = {
        list        : [],
        tableName   :   "permission_title",
        filters      : [
            {
                title : "name",
                type  : "text",
                field : "permissionName",
                default : true
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            },
            {
                name : "permissionName",
                title : "name",
                orderBy : true
            },
            {
                name : "note",
                title : "permission_note"
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentPermission = item;
            $timeout(function () {
                applicationService.action($scope,"permissionUsers",className.permissions,"getUsersByPermission",{
                    permissionId : item.id
                });

                applicationService.action($scope,"permissionRoles",className.permissions,"getRolesByPermission",{
                    permissionId : item.id
                });
            },1000);
        },
        actions : [
            {
                icon : "fa-plus",
                color : "#46BE28",
                tooltip : "permission_create_permission",
                actionCallback : function (ev){
                    $scope.showDialog(ev,$scope,"createPermission.html",function(answer){
                        applicationService.create($scope,"createPermissionResult", className.permissions,answer,function(result){
                            $scope.showToast($scope.getResultMessage(result));
                            permissionList();
                        });
                    });
                }
            }
        ],
        deleteOptions : {
            deleteCallback : function (ev,item) {
                $scope.showConfirmDialog(
                    ev,
                    $scope.translate("permission_delete_message") + " " + item.permissionName,
                    $scope.translate("permission_delete_content_message"),
                    'Delete permission',
                    $scope.translate("delete"),
                    $scope.translate("cancel"),
                    function () {
                        applicationService.remove($scope, "", className.permissions, item.id, function (result) {
                            $scope.showToast($scope.getResultMessage(result));
                            permissionList();
                            $scope.currentPermission = null;
                        });
                    }
                )
            },
            deleteTitle : "permission_delete_permission"
        }
    };

    permissionList();

    $scope.updatePermission = function() {
        applicationService.update($scope,"",className.permissions,$scope.currentPermission,function(result){
            $scope.showToast($scope.getResultMessage(result));
        });
    };

});