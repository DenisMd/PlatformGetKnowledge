model.controller("headerController",function($scope){
    $scope.getBack = function () {
        var url = urlStack.back();
        $scope.goTo($scope.createUrl(url),true);
    };
    $scope.getNext = function () {
        var url = urlStack.next();
        $scope.goTo($scope.createUrl(url),true);
    }
});