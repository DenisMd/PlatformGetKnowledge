var model = angular.module("mainApp", ["BackEndService"]);
model.controller("mainController", function ($scope, $http, applicationService) {

  applicationService.pageInfo($scope);

  //applicationService.read($scope, "menu" , "com.getknowledge.modules.menu.Menu", 1);
  applicationService.action($scope, "menu" , "com.getknowledge.modules.menu.Menu" , "getMenuByName" , {"name" : "General"});
});