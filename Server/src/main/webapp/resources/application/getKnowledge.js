var model = angular.module("mainApp", ["BackEndService"]);
model.controller("mainController", function ($scope, $http, applicationService) {

  applicationService.pageInfo($scope);

  applicationService.list($scope, "menu" , "com.getknowledge.modules.menu.Menu");
  //applicationService.action($scope, "menu" , "com.getknowledge.modules.menu.Menu" , "getMenu" , {});
});