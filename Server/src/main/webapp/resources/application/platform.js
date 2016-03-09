;
String.prototype.capitalizeFirstLetter = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
};

angular.module("BackEndService", ['ui.router','ngSanitize','ngScrollbars','angular-loading-bar','ngAnimate','angularFileUpload'])
    .factory('className', function() {
        return {
            "userInfo" : "com.getknowledge.modules.userInfo.UserInfo",
            "menu" : "com.getknowledge.modules.menu.Menu",
            "menuItem" : "com.getknowledge.modules.menu.item.MenuItem",
            "video" : "com.getknowledge.modules.video.Video",
            "language" : "com.getknowledge.modules.dictionaries.language.Language",
            "country" : "com.getknowledge.modules.dictionaries.country.Country",
            "region" : "com.getknowledge.modules.dictionaries.region.Region",
            "city" : "com.getknowledge.modules.dictionaries.city.City",
            "section" : "com.getknowledge.modules.section.Section",
            "bootstrap_services" : "com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo",
            "permissions" : "com.getknowledge.platform.modules.permission.Permission",
            "roles" : "com.getknowledge.platform.modules.role.Role",
            "users" : "com.getknowledge.platform.modules.user.User",
            "system_event" : "com.getknowledge.modules.event.SystemEvent",
            "tasks" : "com.getknowledge.platform.modules.task.Task",
            "trace" : "com.getknowledge.platform.modules.trace.Trace",
            "knolwedge" : "com.getknowledge.modules.dictionaries.knowledge.Knowledge",
            "settings" : "com.getknowledge.modules.settings.Settings",
            "systemServices" : "com.getknowledge.platform.modules.service.Service",
            "socialLinks" : "com.getknowledge.modules.socialLinks.SocialLink",
            "hpMessage" : "com.getknowledge.modules.help.desc.HpMessage",
            "groupCourses" : "com.getknowledge.modules.courses.group.GroupCourses",
            "groupBooks" : "com.getknowledge.modules.books.group.GroupBooks",
            "groupPrograms" : "com.getknowledge.modules.programs.group.GroupPrograms",
            "programmingLanguages" :  "com.getknowledge.modules.dictionaries.programming.languages.ProgrammingLanguage",
            "programmingStyles" : "com.getknowledge.modules.dictionaries.programming.styles.ProgrammingStyles",
            "book" : "com.getknowledge.modules.books.Book",
            "program" : "com.getknowledge.modules.programs.Program"
         };
    })
    .factory('modules',function(){
        return ["user","accept","section","restorePassword","groupCourses","groupBooks","groupPrograms","book","program"];
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

        var language;
        this.setLanguage = function(lang){
            language = lang;
        };
        this.getLanguage = function(){
            return language;
        };

        var onLogoutFun;
        this.onLogout = function(){
            if (angular.isFunction(onLogoutFun)){
                onLogoutFun();
            }
        };

        this.setOnLogout  = function(fun){
            if (angular.isFunction(fun)){
                onLogoutFun = fun;
            }
        };
    })
    .service("applicationService", function ($http,$stateParams,$sce,FileUploader,pageService,modules,resourceUrl,errorService) {
        "use strict";

        var platformDataUrl = "/data/";

        this.pageInfo = function($http,$stateParams,$sce,pageService,modules,resourceUrl,errorService){
            var application;
            var moduleUrl = "";
            var language = $stateParams.language? $stateParams.language:pageService.getLanguage();
            return $http.get(resourceUrl + 'page-info/pageInfo.json')
                .then(function (response) {
                    var data = response.data;
                    application = data;
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
                    console.log("Error loading page translation(" + error.config.url + ")");
                });
        };

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

        function filter(className,first,max) {
            this.className = className;
            this.first = first;
            this.max = max;
            this.result = {first : this.first, max : this.max};

            this.increase = function (value) {
                this.result.first = this.result.first + value;
            };

            this.setOrder = function(order,desc) {
                if (!("order" in this.result)) {
                    this.result.order = [];
                }

                this.result.order.push({"field" : order , "route" : desc ? "Desc" : "Asc"});
            };

            this.searchText = function(fields) {
                this.result.searchText = fields;
            };

            this.in = function (fieldName, values) {
                this.result.in = {
                    fieldName : fieldName,
                    values : values
                }
            };

            this.equal = function (fieldName, value) {
              if (!this.result.equal) {
                  this.result.equal = [];
              }
              this.result.equal.push({
                  fieldName : fieldName,
                  value : value
              });
            };

            this.clearEqual = function() {
                delete this.result.equal;
            };

            this.clearIn = function (fieldName, values) {
                delete this.result.in;
            };

            this.clearOrder = function () {
                this.result.order = [];
            };

            this.reload = function () {
                this.result.first = 0;
            }
        }

        this.createFilter = function(className,first,max) {
              return new filter(className,first,max);
        };

        this.filterRequest = function ($scope,name,filter,callback) {
            var isCallbackFunction = isFunction(callback);
            $http({
                method: 'POST',
                url: platformDataUrl+'filter',
                data: $.param({className: filter.className,
                    properties : JSON.stringify(filter.result)}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data){
                if (name && data)
                    $scope[name] = data;
                if (isCallbackFunction && data){
                    if (angular.isArray(data.list)){
                        data.list.forEach(function(item,i,array){
                            callback(item,i,array,data.creatable);
                        });
                    }
                }
            }).error(function(error, status, headers, config){
                errorService.showError(error,status);
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
                if (name)
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
                if (name)
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

        this.createUploader = function ($scope,name,className,actionName,data,callback){
            "use strict";
            var isCallbackFunction = isFunction(callback);
            var formData = {
                className: className,
                actionName: actionName,
                data: JSON.stringify(data)
            };
            var uploader = new FileUploader({
                url: platformDataUrl+'actionWithFile',
                autoUpload: false,
                onBeforeUploadItem: function(item) {
                    item.formData.push(formData);
                }
            });

            uploader.onSuccessItem = function(fileItem, response, status, headers) {
                var data = response;
                if (isCallbackFunction)
                    callback(data);
            };
            uploader.onErrorItem = function(fileItem, response, status, headers) {
                errorService.showError(response,status);
            };

            return uploader;

        };

        this.actionWithFile = function ($scope,name,className,actionName,data,files,callback){
            "use strict";
            var isCallbackFunction = isFunction(callback);
            var formData = {
                className: className,
                actionName: actionName,
                data: JSON.stringify(data)
            };
            if (files) {
                var uploader = new FileUploader({
                    url: platformDataUrl+'actionWithFile',
                    autoUpload: false,
                    onBeforeUploadItem: function(item) {
                        item.formData.push(formData);
                    }
                });

                uploader.onSuccessItem = function(fileItem, response, status, headers) {
                    var data = response;
                    if (isCallbackFunction)
                        callback(data);
                };
                uploader.onErrorItem = function(fileItem, response, status, headers) {
                    errorService.showError(response,status);
                };

                if (Array.isArray(files)){
                    uploader.queue = files;
                } else {
                    uploader.addToQueue(files);
                }
                uploader.uploadAll();
            } else {
                $http({
                    method: 'POST',
                    url: platformDataUrl+'actionWithFile',
                    headers: {
                        'Content-Type': undefined
                    },
                    data: formData
                }).success(function (data){
                    success(data);

                }).error(function(error, status, headers, config){
                    errorService.showError(error,status);
                });
            }

            function success(data){
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
            }
        };

        this.create = function ($scope,name,className,data,callback){
            $http({
                method: 'POST',
                url: platformDataUrl+'create',
                data: $.param({className: className,
                    object : JSON.stringify(data)}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data){
                if (name)
                    $scope[name] = data;
                if (isFunction(callback))
                    callback(data);
            }).error(function(error, status, headers, config){
                errorService.showError(error,status);
            });
        };

        this.update = function ($scope,name,className,data,callback){
            $http({
                method: 'POST',
                url: platformDataUrl+'update',
                data: $.param({className: className,
                    object : JSON.stringify(data)}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data){
                if (name)
                    $scope[name] = data;
                if (isFunction(callback))
                    callback(data);
            }).error(function(error, status, headers, config){
                errorService.showError(error,status);
            });
        };

        this.remove = function ($scope,name,className,id,callback) {
            $http.get(platformDataUrl+"remove?className="+className+"&id="+id).success(function(data){
                if (name)
                    $scope[name] = data;
                if (isFunction(callback)) {
                    callback(data);
                }
             }).error(function(error, status, headers, config){
                errorService.showError(error,status);
            });
        };

        this.imageHref = function(className,id){
            if (!className || !id) return "";
            return "/data/image?className="+className+"&id="+id;
        };

        this.fileHref = function(className,id,key){
            if (!className || !id) return "";
            return "/data/readFile?className="+className+"&id="+id+"&key="+key;
        };

        this.fileByKeyHref = function(className,id,key){
            if (!className || !id) return "";
            return "/data/readFile?className="+className+"&id="+id+"&key="+key;
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
            error = errorMessage?errorMessage:{};
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

    .provider('applicationProvider', function ApplicationServiceProvider() {
        this.$get= ['$http','$stateParams','$sce','pageService','modules','resourceUrl','errorService',function applicationServiceFactory($http,$stateParams,$sce,pageService,modules,resourceUrl,errorService){
            return new applicationService($http,$stateParams,$sce,pageService,modules,resourceUrl,errorService);
        }];

    })

    .config(function ($stateProvider, $urlRouterProvider,$urlMatcherFactoryProvider,applicationServiceProvider,resourceTemplate) {
        //console.log(applicationServiceProvider.$get.pageInfo());
        var pageInfo = function($http,$stateParams,$sce,pageService,modules,resourceUrl,errorService){
            var application;
            var moduleUrl = "";
            var language = $stateParams.language? $stateParams.language:pageService.getLanguage();
            return $http.get(resourceUrl + 'page-info/pageInfo.json')
                .then(function (response) {
                    var data = response.data;
                    application = data;
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
                    console.log("Error loading page translation(" + error.config.url + ")");
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
            resolve: {
                pageInfo : pageInfo
            },
            templateUrl: "/404",
            controller : function($rootScope,pageInfo, $scope){
                $rootScope.application = pageInfo;
            }
        })
            .state("accessDenied",{

                resolve: {
                    pageInfo : pageInfo
                },
                templateUrl: "/accessDenied",
                controller : function($rootScope,pageInfo, $scope){
                    $rootScope.application = pageInfo;
                }
            });
        $urlRouterProvider.otherwise(function($injector) {

            var $state = $injector.get('$state');

            $state.go('404', null, {
                location: false
            });

        });
    })

    .run(function($rootScope,$state,$q,pageService){
        angular.element("body").append("<error-modal-template></error-modal-template>");
        $rootScope.$on('$stateChangeStart',
            function(event, toState, toParams, fromState, fromParams) {
                if (toParams && toParams.language) {
                    pageService.setLanguage(toParams.language);
                }

            });
        $rootScope.$on('$stateChangeError', function(event, toState, toParams, fromState, fromParams, error) {
            event.preventDefault();
            if (toParams && toParams.language) {
                pageService.setLanguage(toParams.language);
            } else {
                pageService.setLanguage("en");
            }
            switch (error.status) {
                case 403 : $state.go('accessDenied');
                    break;
                default : $state.transitionTo('404',null, {
                    location: false
                });
            }
        });
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
            link: function(scope, element, attrs) {
                scope.getContentUrl = function() {
                    return resourceTemplate + attrs.name + ".html";
                };
            },
            template: '<div ng-include="getContentUrl()"></div>',
            controller : function($scope,$attrs,$parse,$interpolate){
                $scope.data = $scope[$attrs.data];
                $scope.$watch($attrs.data, function(value,oldValue) {
                    $scope.data = value;
                    if (value != oldValue) {
                        if ($scope.updateValues && angular.isFunction($scope.updateValues)){
                            $scope.updateValues();
                        }
                    }

                });
                $scope.getData = function (){
                    return $scope.data;
                };
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