var model = angular.module("mainApp", ["BackEndService"]);
model.controller("mainController", function ($scope, $http, applicationService) {

  $scope.translate = function (key) {
      if (!(key in $scope.application.text)) {
          return key;
      }

      return $scope.application.text[key];
  };

  applicationService.pageInfo($scope);

  applicationService.list($scope, "menu" , "com.getknowledge.modules.menu.Menu");
  //applicationService.action($scope, "menu" , "com.getknowledge.modules.menu.Menu" , "getMenu" , {});
});