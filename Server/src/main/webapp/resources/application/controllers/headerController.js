model.controller("headerController",function($scope){

    //Сварачивает и разварачивает меню
    $scope.toggelClick = function () {
        $scope.toggelMenu = !$scope.toggelMenu;
        var wrapper = angular.element("#wrapper");
        wrapper.toggleClass("wrapper-left");

        if (plUtils.isFunction($scope.getData().toggelClickCallback)) {
            $scope.getData().toggelClickCallback();
        }
    };

});