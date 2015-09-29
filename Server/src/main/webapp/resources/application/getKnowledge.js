var model = angular.module("mainApp", ["BackEndService"]);
model.controller("mainController", function ($scope, $http, applicationService) {

  applicationService.pageInfo($scope);
  applicationService.read($scope, "list" ,"com.getknowledge.modules.userInfo.UserInfo",1);

  $scope.info = ["Banana", "Orange", "Apple", "Mango"];

});