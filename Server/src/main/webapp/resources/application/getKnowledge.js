var model = angular.module("mainApp", ["BackEndService","ngAnimate"]);
model.controller("mainController", function ($scope, $http, $state, applicationService) {
    $scope.menuScrollConfig = {
        autoHideScrollbar: false,
        theme: 'light-3',
        advanced:{
            updateOnContentResize: true
        }
    };

    //смена языка
    $scope.changeLanguage = function (language) {
        if (!$scope.application.language || $scope.application.language===language) {
            return;
        }
        var str = window.location.hash.split("/").splice(2).join("/");
        if (str) {
            $state.go("modules", {
                language: language,
                path: str
            });
        } else {
            $state.go("home", {
                language: language
            });
        }
        applicationService.pageInfo($scope);
    };

    $scope.toggelMenu = true;
    $scope.toggelClick = function(){
        $scope.toggelMenu = !$scope.toggelMenu;
        var wrapper = angular.element("#wrapper");
        wrapper.toggleClass("wrapper-left");
    };

    $scope.translate = function (key) {
      if (!$scope.application || !$scope.application.text || !(key in $scope.application.text)) {
          return key;
      }

      return $scope.application.text[key];

    };

  applicationService.pageInfo($scope);

  applicationService.action($scope, "menu" , "com.getknowledge.modules.menu.Menu" , "getMenu" , {});
});
