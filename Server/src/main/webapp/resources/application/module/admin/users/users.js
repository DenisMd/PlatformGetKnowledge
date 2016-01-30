model.controller("usersCtrl", function ($scope, applicationService, className,$mdDialog) {

    //иницилизация
    $scope.showAutoCompleteForRight = false;
    $scope.showDeleteColumn = false;
    $scope.users = [];

    applicationService.count($scope,"countUsers",className.userInfo);
    applicationService.list($scope,"listRoles",className.roles);

    var filter = applicationService.createFilter(className.userInfo,0,10);

    var addUsers = function(user){
        $scope.users.push(user);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"",filter,addUsers);
    };

    doAction();

    var reverse = false;
    $scope.setUserOrder = function(orderName) {
        reverse = !reverse;

        filter.clearOrder();
        filter.setOrder(orderName,reverse);
        filter.first = 0;
        $scope.users = [];
        doAction();
    };

    $scope.searchUsers = function(text) {
        if (text) {
            var splitArray = text.split(".");
            if (splitArray.length > 1) {
                var searchFields = {fields: [{"firstName": splitArray[0]}, {"lastName": splitArray[1]}]};
                filter.searchText(searchFields);
            } else {
                var searchFields = {fields: [{"firstName": text}, {"lastName": text},{"user.login" : text}], or: true};
                filter.searchText(searchFields);
            }
        }
        $scope.users = [];
        filter.first = 0;
        doAction();
    };

    $scope.showDeleteDialog = function(ev) {
        var confirm = $mdDialog.confirm()
            .title($scope.translate("user_remove") + " " + $scope.currentUser.user.login)
            .textContent()
            .targetEvent(ev)
            .ariaLabel('Delete user')
            .ok($scope.translate("delete"))
            .cancel($scope.translate("cancel"));
        $mdDialog.show(confirm).then(function() {
            applicationService.remove($scope,"",className.userInfo,$scope.currentUser.id,function (result) {
                $scope.showToast(result);
                doAction();
            });
        });
    };


    $scope.setCurrentItem = function (item) {
        $scope.currentUser = item;
        $scope.defaultRoleName = item.user.role.roleName;
        $scope.showAutoCompleteForRight = false;
        $scope.showDeleteColumn = false;
    };

    $scope.loadMore = function () {
        filter.increase(10);
        doAction();
    };

    $scope.roleData = {
        "id" : "roles",
        "count" : 1,
        "filter":"roleName",
        "class" : "input-group-sm",
        "listName" : "listRoles",
        "required" : true,
        "defaultValue" : "defaultRoleName",
        "callback" : function (value){
            $scope.currentUser.user.role = value;
        }
    };

    $scope.updateUser = function() {
        applicationService.update($scope,"",className.users,$scope.currentUser.user,function(result){
            $scope.showToast(result);
        });
    };

    var updateFilterPermissions = function (item) {
        var isContain = false;
        $scope.currentUser.user.permissions.forEach(function(element){
            if (element.permissionName == item.permissionName) {
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
            if ($scope.currentUser.user.permissions[i].id == id) {
                $scope.currentUser.user.permissions.splice(i,1);
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
            $scope.currentUser.user.permissions.push(value);
            $scope.showAutoCompleteForRight = false;
        }
    };


});