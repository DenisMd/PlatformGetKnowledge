model.controller("userCtrl", function ($scope, $state,$http,applicationService) {
    applicationService.pageInfo($scope.$parent);
    $scope.test = "TEST!";
});