;
angular.module("BackEndService", ['ui.router','ngSanitize','ngScrollbars','angular-loading-bar','ngAnimate'])
    .factory('className', function() {
        return {
            "userInfo" : "com.getknowledge.modules.userInfo.UserInfo",
            "menu" : "com.getknowledge.modules.menu.Menu",
            "video" : "com.getknowledge.modules.video.Video",
            "language" : "com.getknowledge.modules.dictionaries.language.Language",
            "registerInfo" : "com.getknowledge.modules.userInfo.registerInfo.RegisterInfo"
        };
    })
    .factory('modules',function(){
        return ["user" , "accept"];
    })
    .constant("resourceUrl", "/resources/application/")
    .constant("resourceTemplate","/resources/template/")
    .service("pageService",function(){
        this.getPathVariable = function (key,path) {
            if (!key) return "";

            var urlSplit = path.split("/");

            for (var i=0; i < urlSplit.length; i++) {
                if (urlSplit[i] == key) {
                    return i == (urlSplit.length-1) ? "" : urlSplit[i+1];
                }
            }

            return "";
        };

        var lang;
        this.setLanguage = function(lang){

        }
    })
    .service("applicationService", function ($http,$sce,modules,resourceUrl,errorService) {
        "use strict";

        var platformDataUrl = "/data/";


        this.login = function ($scope,name, user, pass,callback) {
            var isCallbackFunction = isFunction(callback);
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
                if (isCallbackFunction) {
                    callback(data);
                }
            });
        };

        this.read = function($scope, name, className, id, callback) {
            var isCallbackFunction = isFunction(callback);
            $http.get(platformDataUrl+"read?className="+className+"&id="+id)
                .success(function(data){
                    $scope[name] = data;
                    if (isCallbackFunction) {
                        callback(data);
                    }
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
                    data.forEach(function(item,i,array){
                        callback(item,i,array);
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
                    data.forEach(function(item,i,array){
                        callback(item,i,array);
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
                        data.forEach(function(item,i,array){
                            callback(item,i,array);
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
        var pageInfo = function($http, $stateParams,$sce,pageService,errorService,resourceUrl,modules){
            var application;
            var moduleUrl = "";
            var language = $stateParams.language;
            return $http.get(resourceUrl + 'page-info/pageInfo.json')
                .then(function (response) {
                    var data = response.data;
                    application = data;
                    language = $stateParams.language;
                    var moduleUrlSplit = $stateParams.path? $stateParams.path.split("/"):"";
                    for (var i = 0; i < moduleUrlSplit.length; i++) {
                        var isContains = false;
                        for (var j = 0; j < modules.length; j++) {
                            if (modules[j] == moduleUrlSplit[i - 1]) {
                                isContains = true;
                                break;
                            }
                        }
                        if (isContains) continue;
                        moduleUrl += "/" + moduleUrlSplit[i];
                    }
                 return $http.get(resourceUrl + "page-info/" + language + ".json")
                }).then(function(response) {
                    var data = response.data;
                    application.text = {};
                    for (var stingData in data.text) {
                        application.text[stingData] = $sce.trustAsHtml(data.text[stingData]);
                    }

                    application.language = data.language;

                    if (moduleUrl) {
                        return $http.get(resourceUrl + "module" + moduleUrl + "/page-info/pageInfo.json")
                    } else return application;
                }).then(function (response) {
                    if (response === application) return application;

                    var data = response.data;
                    for (var key in data) {
                        if (key != "text") {
                            application[key] = data[key];
                        }
                    }
                    return $http.get(resourceUrl + "module" + moduleUrl + "/page-info/" + language + ".json")
                }).then(function (response) {
                    if (response === application) return application;

                    var data = response.data;
                    for (var key in data.text) {
                        if (application.text[key]) {
                            continue;
                        }
                        application.text[key] = data.text[key];
                    }
                    return application;
                }, function(error) {
                    errorService.showError("Error loading page translation(" + error. error.config.url + ")", status);
                });
        };

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

        function getCtrl ($stateParams,$rootScope,pageInfo,modules){
            $rootScope.application = pageInfo;
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
            resolve: {
                pageInfo : pageInfo
            },
            views : {
                '' : {
                    templateUrl : resourceTemplate + 'indexTemplate.html',
                    controller : function($rootScope,pageInfo){
                        $rootScope.application = pageInfo;
                    }
                }
            }
        }).state('modules',{
            url : '/:language/{path:nonURIEncoded}',
            resolve: {
                pageInfo : pageInfo
            },
            views : {
                '' : {
                    templateUrl : getURL,
                    controllerProvider : getCtrl

                }
            }
        })  .state("404",{
            templateUrl: "/404"
        })
            .state("accessDenied",{
                templateUrl: "/accessDenied"
            });
    })

    .run(function($rootScope){
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
    })

    .directive("useValidation", function () {
        return {
            restrict: 'A',
            require:'ngModel',
            scope:{
                type:"@useValidation",
                options: "=options"
            },
            link:function (scope, elm, attrs,ngModel) {
                if (!scope.type) return;
                switch (scope.type){

                    case "compareTo":
                        ngModel.$validators.compareTo = function(value){
                            return scope.options.value === value;
                        };
                        scope.$watch("options", function() {
                            ngModel.$validate();
                        });
                        break;

                    default : return;
                }
            }
        };
    });