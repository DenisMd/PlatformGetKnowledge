model.controller("rolesCtrl", function ($scope, applicationService, className,$mdDialog) {

    function roleList() {
        $scope.selectorData.list = [];
        applicationService.list($scope, "roles", className.roles,function (item) {
            $scope.selectorData.list.push(item);
        });
    }

    $scope.selectorData = {
        list        : [],
        tableName   :   "roles_title",
        filters      : [
            {
                title : "name",
                type  : "text",
                field : "roleName",
                default : true
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            },
            {
                name : "roleName",
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

            applicationService.action($scope,"permissionUsers",className.permissions,"getUsersByPermission",{
                permissionId : item.id
            });

            applicationService.action($scope,"permissionRoles",className.permissions,"getRolesByPermission",{
                permissionId : item.id
            });
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
                        });
                    }
                )
            },
            deleteTitle : "permission_delete_permission"
        }
    };

    roleList();

    $scope.setCurrentItem = function (item) {
        $scope.currentRole = item;
        $scope.showAutoCompleteForRight = false;
        $scope.showDeleteColumn = false;
    };

    $scope.updateRole = function(){
        applicationService.update($scope,"updateResult",className.roles,$scope.currentRole,function(result){
            $scope.showToast(result);
        });
    };

    $scope.showAdvanced = function(ev) {
        $scope.showDialog(ev,$scope,"createRole.html",function(answer){
            applicationService.create($scope,"", className.roles,answer,function(result){
                $scope.showToast(result);
                applicationService.list($scope , "roles", className.roles);
            });
        });
    };

    $scope.showDeleteDialog = function(ev) {
        var confirm = $mdDialog.confirm()
            .title($scope.translate("role_deleteRole") + " " + $scope.currentRole.roleName)
            .textContent($scope.translate("role_deleteContentMessage"))
            .targetEvent(ev)
            .ariaLabel('Delete role')
            .ok($scope.translate("delete"))
            .cancel($scope.translate("cancel"));
        $mdDialog.show(confirm).then(function() {
            applicationService.remove($scope,"",className.roles,$scope.currentRole.id,function (result) {
                $scope.showToast(result);
                applicationService.list($scope , "roles", className.roles);
            });
        });
    };

    var updateFilterPermissions = function (item) {
        var isContain = false;
        $scope.currentRole.permissions.forEach(function(element){
            if (element.permissionName === item.permissionName) {
                isContain = true;
                return;
            }
        });
        if (!isContain) {
            $scope.filterPermissions.push(item);
        }
    };

    $scope.showAutoCompleteForRight = false;
    $scope.showDeleteColumn = false;
    $scope.addNewPermission = function() {
        $scope.showAutoCompleteForRight = !$scope.showAutoCompleteForRight;
        $scope.filterPermissions = [];
        if ($scope.listPermissions) {
            $scope.listPermissions.forEach(function(item){
               updateFilterPermissions(item);
            });
        } else {
            applicationService.list($scope, "listPermissions", className.permissions, function (item) {
                updateFilterPermissions(item);
            });
        }
    };

    $scope.removePermission = function(id){
        for (var i=0; i < $scope.currentRole.permissions.length; i++) {
            if ($scope.currentRole.permissions[i].id === id) {
                $scope.currentRole.permissions.splice(i,1);
                return;
            }
        }
    };

    $scope.permissionsData = {
        "id" : "permissions",
        "count" : 1,
        "filter":"permissionName",
        "class" : "input-group-sm",
        "listName" : "filterPermissions",
        "required" : true,
        "callback" : function (value){
            $scope.currentRole.permissions.push(value);
            $scope.showAutoCompleteForRight = false;
        }
    };


});