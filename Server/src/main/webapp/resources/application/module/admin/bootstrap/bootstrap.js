model.controller("bootstrapCtrl", function ($scope, $state,$http,applicationService,pageService,className,$mdToast,$mdDialog,$mdMedia) {

    $scope.updateService = function() {

      applicationService.update($scope,"updateResult",className.bootstrap_services,$scope.currentService,function(result){
          $mdToast.show(
              $mdToast.simple()
                  .textContent(result)
                  .position("bottom right")
                  .hideDelay(3000)
          );
      });
    };

    applicationService.list($scope , "bootstrap_services",className.bootstrap_services);

    $scope.setCurrentItem = function (item) {
        $scope.currentService = item;
    };


    $scope.showAdvanced = function(ev) {
        var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'))  && $scope.customFullscreen;
        $mdDialog.show({
                controller: DialogController,
                templateUrl: 'myModalContent.html',
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose:true,
                fullscreen: useFullScreen,
                locals: {
                    theScope: $scope
                }
            })
            .then(function(answer) {
                applicationService.action($scope,"bootstrapResult" , className.bootstrap_services,"do",answer,function(result){
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent(result)
                            .position("bottom right")
                            .hideDelay(3000)
                    );
                    applicationService.list($scope , "bootstrap_services",className.bootstrap_services);
                });
            });
        $scope.$watch(function() {
            return $mdMedia('xs') || $mdMedia('sm');
        }, function(wantsFullScreen) {
            $scope.customFullscreen = (wantsFullScreen === true);
        });
    };

});