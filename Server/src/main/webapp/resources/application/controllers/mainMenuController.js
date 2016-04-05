model.controller("mainMenuController",function($scope,applicationService,$http,className,pageService){

    var loadMenu = function(){
        applicationService.action($scope, "menu", className.menu, "getMenu", {}, function(menu){
            if (plUtils.isFunction($scope.getData().callback)){
                $scope.getData().callback(menu);
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
                pageService.onLogout();
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

});