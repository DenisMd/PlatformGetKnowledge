model.controller("headerController",function($scope){

    //Сварачивает и разварачивает меню
    $scope.toggelClick = function () {
        $scope.toggelMenu = !$scope.toggelMenu;
        var wrapper = angular.element("#wrapper");
        wrapper.toggleClass("wrapper-main-content");

        var siteFooter = angular.element("#footer-page");
        siteFooter.toggleClass("footer-margin");

        if (plUtils.isFunction($scope.getData().toggelClickCallback)) {
            $scope.getData().toggelClickCallback();
        }
    };

});