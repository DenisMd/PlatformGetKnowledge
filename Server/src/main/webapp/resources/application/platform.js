;
var modules = ["user" , "accept"];
angular.module("BackEndService", ['ui.router','ngSanitize','ngScrollbars'])
    .constant("resourceUrl", "/resources/application/")
    .constant("resourceTemplate","/resources/template/")
    .service("pageService",function(){
        this.getLanguage = function(){
            var url = window.location.hash.split("/");
            return url[1];
        }
    })
    .service("applicationService", function ($http,$sce,resourceUrl,errorService) {
        "use strict";

        var platformDataUrl = "/data/";

        this.getPathVariable = function (key) {
            if (!key) return "";

            var currentUrl = window.location.hash;
            var urlSplit = currentUrl.split("/");

            for (var i=0; i < urlSplit.length; i++) {
                if (urlSplit[i] == key) {
                    return i == (urlSplit.length-1) ? "" : urlSplit[i+1];
                }
            }

            return "";
        };

        this.pageInfo = function ($scope) {
            $http.get(resourceUrl + 'page-info/pageInfo.json').success(function(data) {
                $scope.application = data;
                var currentUrl = window.location.hash;
                var urlSplit = currentUrl.split("/");
                var language = urlSplit[1];
                var moduleUrlSplit = urlSplit.splice(2);
                var moduleUrl = "";
                for (var i=0; i < moduleUrlSplit.length; i++) {
                    var isContains = false;
                    for (var j=0; j < modules.length; j++) {
                        if (modules[j] == moduleUrlSplit[i-1]) {
                            isContains = true;
                            break;
                        }
                    }
                    if (isContains) continue;
                    moduleUrl += "/" + moduleUrlSplit[i];
                }
                $http.get(resourceUrl+"page-info/"+language+".json")
                    .success(function(data){
                    $scope.application.text = {};

                    for (var stingData in data.text){
                        $scope.application.text[stingData] = $sce.trustAsHtml(data.text[stingData]);
                    }

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
                        ).error(function(error, status, headers, config){
                                errorService.showError(error,status);
                        });
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
                        ).error(function(error, status, headers, config){
                                errorService.showError(error,status);
                        });
                    }
                })
                    .error(function(error, status, headers, config){
                    errorService.showError(error,status);
                });
            }).error(function(error, status, headers, config){
                errorService.showError(error,status);
            });
        };

        this.login = function ($scope,name, user, pass,callback) {
            $http({
                method: 'POST',
                url: "/j_spring_security_check",
                data:  $.param({
                    username: user,
                    password: pass
                }),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data) {
                $scope[name] = data;
                callback(data);
            });
        };

        this.read = function($scope, name, className, id, callback) {
            $http.get(platformDataUrl+"read?className="+className+"&id="+id)
                .success(function(data){
                    $scope[name] = data;
                    callback(data);
                }).error(function(error, status, headers, config){
                    errorService.showError(error,status);
                });

        };

        this.count = function($scope, name, className) {
            $http.get(platformDataUrl+"count?className="+className).success(function(data){
                $scope[name] = data;
            }).error(function(error, status, headers, config){
                errorService.showError(error,status);
            });
        };

        this.list = function ($scope,name,className,callback) {
            var isCallbackFunction = isFunction(callback);

            $http.get(platformDataUrl+"list?className="+className).success(function(data){
                $scope[name] = data;
                if (isCallbackFunction){
                    data.forEach(function(item){
                        callback(item);
                    });
                }
            }).error(function(error, status, headers, config){
                errorService.showError(error,status);
            });
        };

        this.listPartial = function ($scope,name,className,first,max,callback) {
            var isCallbackFunction = isFunction(callback);

            $http.get(platformDataUrl+"listPartial?className="+className+"&first="+first+"&max="+max).success(function(data){
                $scope[name] = data;
                if (isCallbackFunction){
                    data.forEach(function(item){
                        callback(item);
                    });
                }
            }).error(function(error, status, headers, config){
                errorService.showError(error,status);
            });
        };

        this.action = function ($scope,name,className,actionName,data,callback){
            var isCallbackFunction = isFunction(callback);
            $http({
                method: 'POST',
                url: platformDataUrl+'action',
                data: $.param({className: className,
                    actionName:actionName,
                    data : JSON.stringify(data)}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data){
                $scope[name] = data;
                if (isCallbackFunction){
                    if (angular.isArray(data)){
                        data.forEach(function(item){
                            callback(item);
                        });
                    } else {
                        callback(data);
                    }

                }
            }).error(function(error, status, headers, config){
                errorService.showError(error,status);
            });
        };

        this.create = function ($scope,name,className,data){
            $http({
                method: 'POST',
                url: platformDataUrl+'create',
                data: $.param({className: className,
                    object : JSON.stringify(data)}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data){
                $scope[name] = data;
            }).error(function(error, status, headers, config){
                errorService.showError(error,status);
            });
        };

        this.update = function ($scope,name,className,data){
            $http({
                method: 'POST',
                url: platformDataUrl+'update',
                data: $.param({className: className,
                    object : JSON.stringify(data)}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data){
                $scope[name] = data;
            }).error(function(error, status, headers, config){
                errorService.showError(error,status);
            });
        };

        this.remove = function ($scope,name,className,id) {
            $http.get(platformDataUrl+"remove?className="+className+"&id="+id).success(function(data){
                $scope[name] = data;
            });
        };

        this.imageHref = function(className,id){
            if (!className || !id) return "";
            return "/data/image?className="+className+"&id="+id;
        };

        function isFunction(func){
            if (func && angular.isFunction(func)){
                return true;
            }
            return false;
        }
    })

    .service("errorService", function (resourceUrl) {
        "use strict";

        function showModalError(){
            var modal = angular.element('#errorMessage');
            modal.modal('show');
        }

        function hideModalError(){
            angular.element('#errorMessage').modal('hide');
        }

        var error = null;

        this.showErrorCallback = showModalError;
        this.hideErrorCallback = hideModalError;

        this.showError = function(errorMessage,status){
            error = errorMessage;
            error.status = status;
            this.showErrorCallback();
        };

        this.removeError = function(){
            error = null;
            this.hideErrorCallback();
        };

        this.getError = function(){
            return error;
        }

    })

    .config(function ($stateProvider, $urlRouterProvider,$urlMatcherFactoryProvider,resourceTemplate) {

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
            for (var i=0; i < modules.length; i++) {
                if (modules[i] == url [url.length - 2]) {
                    return url [url.length - 2] + "Ctrl";
                }
            }
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
                    templateUrl : resourceTemplate + 'indexTemplate.html'
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
    })

    .run(function($rootScope,applicationService){
        $rootScope.$on('$stateChangeSuccess',
            function(event, toState, toParams, fromState, fromParams){
                applicationService.pageInfo($rootScope);
            });
        angular.element("body").append("<error-modal-template></error-modal-template>");
    })

    .directive("errorModalTemplate",function(resourceTemplate){
        return{
                restrict: "E",
                scope : {},
                controller: function(errorService,$scope){
                    $scope.$watch(
                        function(){ return errorService.getError()},
                        function(newValue,oldValue){
                            if (newValue !== oldValue){
                                $scope.error = newValue;
                            }
                    });
                },
                templateUrl: resourceTemplate+"/error/modalForError.html"
        }
    })

    .directive('moduleTemplate', function(resourceTemplate) {
        return {
            restrict: 'E',
            scope: true,
            templateUrl: function(elem,attrs){
                return resourceTemplate + attrs.name + ".html";
            },
            controller : function($scope,$attrs){
                $scope.data = null;
                $scope.getData = function (){
                    if (!$scope.data){
                        $scope.data = $scope[$attrs.data];
                    }
                    return  $scope.data;
                }
            }
        };
    });