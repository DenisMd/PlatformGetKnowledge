model.controller("mainMenuController",function($scope,applicationService,className){

    var loadMenu = function(){
        applicationService.action($scope, "menu", className.menu, "getMenu", {}, function(menu){
            if (plUtils.isFunction($scope.getData().callback)){
                $scope.getData().callback();
            }
        });
    };

    loadMenu();

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