model.controller("contactsCtrl", function ($scope,className,applicationService) {

    $scope.types = ["Bug","Question","Task","Review","Claim"];

    $scope.sendMessage = function(message) {
      applicationService.action($scope,"",className.hpMessage,"sendHpMessage",message,function(result){
          $scope.showToast(result);
      });
    };

});