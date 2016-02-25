model.controller("editorStylesCtrl", function ($scope,applicationService,className,$mdDialog,$mdMedia) {

    $scope.updateService = function() {

      applicationService.update($scope,"updateResult",className.bootstrap_services,$scope.currentService,function(result){
         $scope.showToast(result);
      });
    };

    applicationService.list($scope , "bootstrap_services",className.bootstrap_services);

    $scope.setCurrentItem = function (item) {
        $scope.currentService = item;
    };


    $scope.showAdvanced = function(ev) {
        $scope.showDialog(ev,$scope,"doBootstrapModal.html",function(answer){
            applicationService.action($scope,"bootstrapResult" , className.bootstrap_services,"do",answer,function(result){
                $scope.showToast(result);
                applicationService.list($scope , "bootstrap_services",className.bootstrap_services);
            });
        });
    };

});