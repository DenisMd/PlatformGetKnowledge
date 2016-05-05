model.controller("usersCtrl", function ($scope, applicationService, className) {

    //иницилизация
    $scope.showAutoCompleteForRight = false;
    $scope.showDeleteColumn = false;

    $scope.selectorData = {
        className   : className.userInfo,
        tableName   :   "user_title",
        loadMoreTitle : "user_load_more",
        filters      : [
            {
                title : "email",
                type  : "text",
                field : "user.login",
                default : true
            },
            {
                title : "id",
                type : "number",
                field : "id"
            },
            {
                title : "user_role",
                type : "enum",
                field : "user.role.roleName",
                constants : []
            },
            {
                title : "user_create_date",
                type : "dateTime",
                field : "user.createDate"
            },{
                title : "user_enabled",
                type : "check_box",
                field : "user.enabled"
            },{
                title : "user_blocked",
                type : "check_box",
                field : "user.blocked"
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            }, {
                name : "user.role.roleName",
                title : "user_role"
            },
            {
                name : "user.login",
                title : "email"
            },
            {
                name : "fullName",
                title : "name"
            },{
                name : "user.createDate",
                title : "user_create_date",
                filter : "date",
                orderBy : true
            },{
                name : "user.enabled",
                title : "user_enabled",
                orderBy : true
            },{
                name : "user.blocked",
                title : "user_blocked",
                orderBy : true
            }
        ],
        callBackForFilter : function(user) {
            user.fullName = user.firstName + ' ' + user.lastName;
        },
        selectItemCallback : function (item) {
            $scope.currentUser = item;
            $scope.defaultRole = item.user.role;
            $scope.showAutoCompleteForRight = false;
            $scope.showDeleteColumn = false;
        },
        actionsForItem : [
            {
                icon : "fa-lock",
                color : "#15206C",
                tooltip : "user_block",
                actionCallback : function (ev,item){
                    $scope.showDialog(ev,$scope,"blockUser.html",function(answer){
                        applicationService.action($scope,"",className.userInfo,"blockUser",{
                            userId : item.id,
                            blockMessage : answer
                        },function(result){
                            $scope.showToast($scope.getResultMessage(result));
                        });
                    });
                }
            },
            {
                icon : "fa-unlock",
                color : "#15206C",
                tooltip : "user_unblock",
                actionCallback : function (ev,item){
                    $scope.showConfirmDialog(
                        ev,
                        $scope.translate("user_unblock") + " " + item.user.login,
                        "",
                        'Unblock user',
                        $scope.translate("unblock"),
                        $scope.translate("cancel"),
                        function () {
                            applicationService.action($scope,"",className.userInfo,"unblockUser",{
                                userId : item.id
                            },function(result){
                                $scope.showToast($scope.getResultMessage(result));
                            });
                        }
                    );
                }
            }
        ]
    };

    $scope.roleData = {
        "id" : "roles",
        "count" : 1,
        "titleField":"roleName",
        "classForInput" : "input-group-sm",
        "listName" : "listRoles",
        "required" : true,
        "defaultValue" : "defaultRole",
        "callback" : function (value){
            $scope.currentUser.user.role = value;
        }
    };

    $scope.permissionsData = {
        "id" : "permissions",
        "count" : 1,
        "titleField":"permissionName",
        "classForInput" : "input-group-sm",
        "listName" : "filterPermissions",
        "required" : true,
        "callback" : function (value){
            $scope.currentUser.user.permissions.push(value);
            $scope.showAutoCompleteForRight = false;
        }
    };

    applicationService.list($scope,"listRoles",className.roles,function(item){
        //2 - index enum role
        $scope.selectorData.filters[2].constants.push(item.roleName);
    });

    $scope.updateUser = function() {
        applicationService.update($scope,"",className.users,$scope.currentUser.user,function(result){
            $scope.showToast($scope.getResultMessage(result));
        });
    };

    var updateFilterPermissions = function (item) {
        var isContain = false;
        $scope.currentUser.user.permissions.forEach(function(element){
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
        for (var i=0; i < $scope.currentUser.user.permissions.length; i++) {
            if ($scope.currentUser.user.permissions[i].id === id) {
                $scope.currentUser.user.permissions.splice(i,1);
                return;
            }
        }
    };


});