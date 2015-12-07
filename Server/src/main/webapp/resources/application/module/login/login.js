model.controller("loginCtrl", function ($scope, $state,$http,applicationService,className) {
    $scope.info = {};
    $scope.login = function() {
        applicationService.login($scope,"loginResult",$scope.info.login,$scope.info.password,function(data){
            if (data.message === 'success') {
                applicationService.action($scope.$parent, "user",className.userInfo, "getAuthorizedUser", {}, function(user){
                    var language = user.language;
                    if (!language){
                        language = $scope.application.language;
                    } else {
                        language = language.name.toLowerCase();
                    }
                    $scope.reloadMenu(function(menu){
                        $state.go($state.$current, {"language": language, path:"user/"+user.id});
                    });
                });
            } else {
                $scope.error = true;
            }
        });
    }
});