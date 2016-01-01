model.controller("bootstrapCtrl", function ($scope, $state,$http,applicationService,pageService,className,$uibModal,$mdToast) {

    $scope.openModal = function (size , item) {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'panelModalContent.html',
            controller: 'panelModalCtrl',
            size: size,
            resolve: {
                item: function () {
                    return item;
                },
                parentScope : function () {
                    return $scope;
                },
                callbackForClose: function(){
                    return;
                }
            }
        });
    };

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

    $scope.panelData = {
        items : [{
            title : "doBootstrap",
            actionName : "do",
            className : className.bootstrap_services,
            columns : [{name : "domain" , type : "text"} ,{name : "email" , type : "text"},{name : "password" , type : "text"},{name : "firstName" , type : "text"},{name : "lastName" , type : "text"},{name : "initPassword" , type : "text"}],
            buttonText  : "doBootstrap",
            label : "fa-cog fa-spin",
            style : {color : '#5E5DD6'}
        }]
    };

    applicationService.list($scope , "bootstrap_services",className.bootstrap_services);

    $scope.setCurrentItem = function (item) {
        $scope.currentService = item;
    };

});