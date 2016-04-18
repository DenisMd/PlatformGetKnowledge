model.controller("loginCtrl", function ($scope,$rootScope,$state,$http,applicationService) {

    $scope.login = function() {
        applicationService.login($scope,"",$scope.loginData.login,$scope.loginData.password,function(data){
            if (data.message === 'success') {
                $scope.getAuthorizedUser(
                    function(user){
                        var language = user.language;
                        if (!language){
                            language = $scope.application.language;
                        } else {
                            language = language.name.toLowerCase();
                        }

                        $rootScope.$emit('reloadMenu', function(menu){
                            $state.go($state.$current, {"language": language, path:"user/"+user.id});
                        });
                });
            } else {
                $scope.loginError = {
                    message : $scope.translate("login_error"),
                    type : 'danger'
                };
            }
        });
    };

});