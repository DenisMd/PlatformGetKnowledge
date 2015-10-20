var model = angular.module("mainApp", ["BackEndService"]);
model.controller("mainController", function ($scope, $http, applicationService) {
    $scope.menuScrollConfig = {
        autoHideScrollbar: false,
        theme: 'light-3',
        advanced:{
            updateOnContentResize: true
        }
    };

  $scope.translate = function (key) {
      if (!$scope.application || !$scope.application.text || !(key in $scope.application.text)) {
          return key;
      }

      return $scope.application.text[key];

  };

  applicationService.pageInfo($scope);

  applicationService.list($scope, "menu" , "com.getknowledge.modules.menu.Menu");
  //applicationService.action($scope, "menu" , "com.getknowledge.modules.menu.Menu" , "getMenu" , {});
});