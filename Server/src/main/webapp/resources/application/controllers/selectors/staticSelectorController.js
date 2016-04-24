model.controller("staticSelectorController" , function ($scope) {
    $scope.tableScroll = {
        theme: 'dark-3',
        setHeight: 400,
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: true
        }
    };

    $scope.orderItem = "";
    $scope.orderReverse = false;
    $scope.setOrder = function (header) {
        if (header.orderBy === true) {
            $scope.orderItem = header.name;
            $scope.orderReverse = !$scope.orderReverse;
        }
    };
});