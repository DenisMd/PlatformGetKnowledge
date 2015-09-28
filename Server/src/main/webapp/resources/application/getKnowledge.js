var model = angular.module("mainApp", ["BackEndService"]);
model.controller("mainController", function ($scope, $http, applicationService) {

  applicationService.pageInfo($scope);

  //TODO : перенести в платформу
  $scope.authenticate = function (user, pass) {
    $http({
      method: 'POST',
      url: "/j_spring_security_check",
      data:  $.param({
        username: user,
        password: pass
      }),
      headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data) {
      alert(data.message);

    }).error(function (error) {
      alert(error);
    });
  };
  applicationService.read($scope, "list" ,"com.getknowledge.modules.userInfo.UserInfo",1);

});