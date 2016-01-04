model.controller("permissionsCtrl", function ($scope,applicationService,className,$mdDialog) {

    $scope.setCurrentItem = function (item) {
        $scope.currentPermission = item;

        applicationService.action($scope,"permissionUsers",className.permissions,"getUsersByPermission",{
            permissionId : item.id
        });

        applicationService.action($scope,"permissionRoles",className.permissions,"getRolesByPermission",{
            permissionId : item.id
        });
    };

    $scope.updatePermission = function() {
        applicationService.update($scope,"updateResult",className.permissions,$scope.currentPermission,function(result){
            $scope.showToast(result);
        });
    };

    applicationService.list($scope , "permissions", className.permissions);


    $scope.showAdvanced = function(ev) {
        $scope.showDialog(ev,$scope,"createPermission.html",function(answer){
            applicationService.create($scope,"createPermissionResult", className.permissions,answer,function(result){
                $scope.showToast(result);
                applicationService.list($scope , "permissions", className.permissions);
            });
        });
    };

    $scope.showDeleteDialog = function(ev) {
        var confirm = $mdDialog.confirm()
            .title($scope.translate("permission_deleteMessage") + " " + $scope.currentPermission.permissionName)
            .textContent($scope.translate("permission_deleteContentMessage"))
            .targetEvent(ev)
            .ariaLabel('Delete permission')
            .ok($scope.translate("delete"))
            .cancel($scope.translate("cancel"));
        $mdDialog.show(confirm).then(function() {
            applicationService.remove($scope,"",className.permissions,$scope.currentPermission.id,function (result) {
                $scope.showToast(result);
                applicationService.list($scope , "permissions", className.permissions);
            });
        });
    };

});