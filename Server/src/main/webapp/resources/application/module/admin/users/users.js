model.controller("usersCtrl", function ($scope, applicationService, className,$mdDialog) {

    $scope.setCurrentItem = function (item) {
        $scope.currentUser = item;
    };

    applicationService.list($scope, "users", className.users);

});