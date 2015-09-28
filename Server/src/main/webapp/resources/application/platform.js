;
angular.module("BackEndService", ['ui.router'])
    .service("applicationService", function ($http) {
        "use strict";

        var resourceUrl = "/resources/application/";
        var platformDataUrl = "/data/";

        this.pageInfo = function ($scope) {
            $http.get(resourceUrl + 'page-info/pageInfo.json').success(function(data) {
                $scope.application = data;
                var currentUrl = window.location.hash;
                var urlSplit = currentUrl.split("/");
                var language = urlSplit[1];
                var moduleUrlSplit = urlSplit.splice(2);
                var moduleUrl = "";
                if(moduleUrlSplit.length) {
                    moduleUrl = moduleUrlSplit.join("/");
                }

                $http.get(resourceUrl+"page-info/"+language+".json").success(function(data){
                    $scope.application.text = data.text;
                    $scope.application.language = data.language;
                    if(moduleUrl) {
                        $http.get(resourceUrl+"module/"+moduleUrl+"/page-info/pageInfo.json").success(
                            function(data) {
                                var key;
                                for(key in data) {
                                    if(key != "text") {
                                        $scope.application[key] = data[key];
                                    }
                                }
                            }
                        );
                        $http.get(resourceUrl+"module/"+moduleUrl+"/page-info/"+language+".json").success(
                            function(data) {
                                var key;
                                for(key in data.text) {
                                    if($scope.application.text[key]) {
                                        continue;
                                    }
                                    $scope.application.text[key] = data.text[key];
                                }
                            }
                        );
                    }
                });
            });
        };

        this.list = function ($scope,name,className) {
            $http.get(platformDataUrl+"list?className="+className).success(function(data){
                $scope[name] = data;
            });
        };

        this.action = function ($scope,name,className,actionName,data){
            $http({
                method: 'POST',
                url: platformDataUrl+'action',
                data: $.param({className: className,
                    actionName:actionName,
                    data : JSON.stringify(data)}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data){
                $scope[name] = data;
            });
        }
    }
)
    .config(function ($stateProvider, $urlRouterProvider,$urlMatcherFactoryProvider) {

        function valToString(val) {
            return val !== null ? val.toString() : val;
        }

        $urlMatcherFactoryProvider.type('nonURIEncoded', {
            encode: valToString,
            decode: valToString,
            is: function () { return true; }
        });

        function getURL ($stateParams){
            return "module/" + $stateParams.path;
        }

        function getCtrl ($stateParams){
            var url = $stateParams.path.split("/");
            return url [url.length - 1] + "Ctrl";
        }
        $urlRouterProvider.when('' , '/ru');
        $urlRouterProvider.when('/' , '/ru');
        $urlRouterProvider.when('/#' , '/ru');
        $urlRouterProvider.when('/#/' , '/ru');

        $stateProvider.state('home', {
            url : "/:language",
            views : {
                '' : {
                    templateUrl : '/resources/template/indexTemplate.html'
                }
            }
        }).state('modules',{
            url : '/:language/{path:nonURIEncoded}',
            views : {
                '' : {
                    templateUrl : getURL,
                    controllerProvider : getCtrl
                }
            }
        });
    });