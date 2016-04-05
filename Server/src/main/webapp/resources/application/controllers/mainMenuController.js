model.controller("mainMenuController",function($scope,$state,$rootScope,applicationService,$http,className){

    var loadMenu = function(callback){
        if (!callback) {
            callback=$scope.getData().callback;
        }

        applicationService.action($scope, "menu", className.menu, "getMenu", {}, function(menu){
            if (plUtils.isFunction(callback)){
                callback(menu);
            }
        });
    };

    loadMenu();

    //Разлогиниваемся
    $scope.logout = function(){
        if (!$scope.user) {
            return;
        }
        $http.get("/j_spring_security_logout").success(function(){
            applicationService.action($scope, "user", className.userInfo, "getAuthorizedUser", {},function(){
                loadMenu();
                $state.go("home",{language:$scope.application.language});
            });
        });
    };

    $scope.menuScrollConfig = {
        theme: 'light-3',
        snapOffset: 100,
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: "ul li"
        }
    };

    $scope.openSocialLink = function(name){
        var object = $.grep($scope.mainLinks, function(e){ return e.name === name; });
        if (object[0].link) {
            $scope.openInNewTab(object[0].link);
        }
    };

    $rootScope.$on('reloadMenu', function (event, callback) {
        loadMenu(callback);
    });
});