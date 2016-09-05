model.controller("rolesCtrl", function ($scope, applicationService, className) {

    function roleList() {
        $scope.selectorData.list = [];
        $scope.selectorData.filters[0].constants = [];
        applicationService.list($scope, "roles", className.roles,function (item) {
            $scope.selectorData.filters[0].constants.push(
                {
                    key : item.roleName,
                    value : item.roleName
                }
            );
            $scope.selectorData.list.push(item);
        });
    }

    $scope.selectorData = {
        list        : [],
        tableName   :   "roles_title",
        filters      : [
            {
                title : "name",
                type  : "enum",
                field : "roleName",
                constants : [],
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
                filter : "memo",
                charLength : 30,
                title : "role_note"
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentRole = item;
            $scope.showAutoCompleteForRight = false;
            $scope.showDeleteColumn = false;
        },
        actions : [
            {
                icon : "fa-plus",
                color : "#46BE28",
                tooltip : "role_create_role",
                actionCallback : function (ev){
                    $scope.showDialog(ev,$scope,"createRole.html",function(answer){
                        applicationService.create($scope,"", className.roles,answer,function(result){
                            $scope.showToast($scope.getResultMessage(result));
                            roleList();
                        });
                    });
                }
            }
        ],
        deleteOptions : {
            deleteCallback : function (ev,item) {
                $scope.showConfirmDialog(
                    ev,
                    $scope.translate("role_delete_role") + " " + item.roleName,
                    $scope.translate("role_delete_content_message"),
                    'Delete role',
                    $scope.translate("delete"),
                    $scope.translate("cancel"),
                    function () {
                        applicationService.remove($scope,"",className.roles,item.id,function (result) {
                            $scope.showToast($scope.getResultMessage(result));
                            roleList();
                            $scope.currentRole = null;
                        });
                    }
                )
            },
            deleteTitle : "role_delete_role"
        }
    };

    roleList();

    $scope.updateRole = function(){
        applicationService.update($scope,"updateResult",className.roles,$scope.currentRole,function(result){
            $scope.showToast($scope.getResultMessage(result));
        });
    };

    //-------------------------------------------------- Логика для добавления и удаления прав для ролей
    $scope.showAutoCompleteForRight = false;
    $scope.showDeleteColumn = false;

    //Информация для отображение в list-input
    $scope.permissionsData = {
        "count"             : 3,
        "titleField"        :"permissionName",
        "classForInput"     : "input-group-sm",
        "listName"          : "filterPermissions",
        "required"          : true,
        "callback" : function (value){
            $scope.currentRole.permissions.push(value);
            $scope.showAutoCompleteForRight = false;
        }
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


});