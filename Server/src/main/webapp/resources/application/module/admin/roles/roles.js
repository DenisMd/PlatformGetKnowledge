model.controller("rolesCtrl", function ($scope, applicationService, className,$mdDialog) {

    $scope.setCurrentItem = function (item) {
        $scope.currentRole = item;
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
            .ariaLabel('Delete permission')
            .ok($scope.translate("delete"))
            .cancel($scope.translate("cancel"));
        $mdDialog.show(confirm).then(function() {
            applicationService.remove($scope,"",className.roles,$scope.currentRole.id,function (result) {
                $scope.showToast(result);
                applicationService.list($scope , "roles", className.roles);
            });
        });
    };

    applicationService.list($scope, "roles", className.roles);
});