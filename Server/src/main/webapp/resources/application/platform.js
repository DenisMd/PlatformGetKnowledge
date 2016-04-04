String.prototype.capitalizeFirstLetter = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
};


function PlatformUtils(){
    this.isFunction = function(func){
        if (func && angular.isFunction(func)){
            return true;
        }
        return false;
    };


}

//Глобальные утилиты
var plUtils = new PlatformUtils();

angular.module("backend.service", ['ui.router','ngSanitize','ngScrollbars','angular-loading-bar','ngAnimate','angularFileUpload'])
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
            "knowledge" : "com.getknowledge.modules.dictionaries.knowledge.Knowledge",
            "settings" : "com.getknowledge.modules.settings.Settings",
            "systemServices" : "com.getknowledge.platform.modules.service.Service",
            "socialLinks" : "com.getknowledge.modules.dictionaries.socialLinks.SocialLink",
            "hpMessage" : "com.getknowledge.modules.help.desc.HpMessage",
            "groupCourses" : "com.getknowledge.modules.courses.group.GroupCourses",
            "groupBooks" : "com.getknowledge.modules.books.group.GroupBooks",
            "groupPrograms" : "com.getknowledge.modules.programs.group.GroupPrograms",
            "programmingLanguages" :  "com.getknowledge.modules.dictionaries.programming.languages.ProgrammingLanguage",
            "programmingStyles" : "com.getknowledge.modules.dictionaries.programming.styles.ProgrammingStyle",
            "book" : "com.getknowledge.modules.books.Book",
            "program" : "com.getknowledge.modules.programs.Program",
            "course" : "com.getknowledge.modules.courses.Course",
            "tutorial" : "com.getknowledge.modules.courses.tutorial.Tutorial"
         };
    })
    .factory('moduleParam',function(){
        //Параметризированные модули(получают параметры из url)
        return ["user","accept","section","restorePassword","groupCourses","groupBooks","groupPrograms","book","program","course","tutorial"];
    })
    .constant("resourceUrl", "/resources/application/")
    .constant("resourceTemplate","/resources/template/")
    .constant("platformDataUrl" , "/data/")
    .service("pageService",function(){
        /**
         * @param {String} key - ключ по которому выбираем значение
         * @param {String} url - путь в котором ищем key
         * @returns {String}
         * @description Функция находить в url ключ и возвращает параметр следующий за ключом
         * */
        this.getPathVariable = function (key,url) {
            if (!(key instanceof String)) {
                return "";
            }

            if (!(url instanceof String)) {
                return "";
            }

            var splitArray = url.split("/");

            for (var i=0; i < splitArray.length; i++) {
                if (splitArray[i] === key) {
                    return i === (splitArray.length-1) ? "" : splitArray[i+1];
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
    .service("applicationService", function ($http,$stateParams,$sce,FileUploader,pageService,moduleParam,resourceUrl,platformDataUrl,errorService) {

        //Настройки приложения
        //TODO: не понятно
        this.applicationProperties = function($http,$stateParams,$sce,pageService,errorService,moduleParam,resourceUrl){
            var applicationData;
            var moduleUrl = "";
            var language = $stateParams.language ? $stateParams.language : pageService.getLanguage();

            //Получаем глобальные настройки из pageInfo приложения
            return $http.get(resourceUrl + 'page-info/pageInfo.json')
                .then(function (response) {

                    applicationData = response.data;
                    var moduleUrlSplit = $stateParams.path ? $stateParams.path.split("/") : [];

                    for (var i = 0; i < moduleUrlSplit.length; i++) {
                        var isContains = false;
                        for (var j = 0; j < moduleParam.length; j++) {
                            if (moduleParam[j] === moduleUrlSplit[i - 1]) {
                                isContains = true;
                                break;
                            }
                        }
                        if (isContains) {
                            continue;
                        }
                        moduleUrl += "/" + moduleUrlSplit[i];
                    }

                    return $http.get(resourceUrl + "page-info/" + language + ".json");

                    //Получаем глобальные переводы
                }).then(function(response) {

                    var data = response.data;
                    applicationData.text = {};

                    for (var key in data.text) {
                        applicationData.text[key] = $sce.trustAsHtml(data.text[key]);
                    }

                    applicationData.language = data.language;

                    if (moduleUrl) {
                        return $http.get(resourceUrl + "module" + moduleUrl + "/page-info/pageInfo.json");
                    } else {
                        return applicationData;
                    }

                    //Если мы находимся в модуле загружаем настройки модуля
                }).then(function (response) {

                    var data = response.data;
                    for (var key in data) {
                        if (key !== "text") {
                            applicationData[key] = data[key];
                        }
                    }

                    //Загружаем переводы модуля
                    return $http.get(resourceUrl + "module" + moduleUrl + "/page-info/" + language + ".json");
                }, function(error) {
                    errorService.showError(error,status);
                    console.error("Error loading application properties (" + error.config.url + ")");
                }).then(function (response) {

                    var data = response.data;
                    for (var key in data.text) {
                        if (applicationData.text[key]) {
                            continue;
                        }
                        applicationData.text[key] = data.text[key];
                    }
                    return applicationData;
                }, function(error) {
                    errorService.showError(error,status);
                    console.error("Error loading page translation(" + error.config.url + ")");
                });
        };

        /**
         * @param {Object} $scope - скопе из которого вызывается метод
         * @param {String} name - имя в скопе в которое запишется response
         * @param {String} email - email пользователя
         * @param {String} pass - пароль введеннный пользователем
         * @param {Function} callback - функция пост-обработки response
         * @return {void}
         * @description - аутенифицирует пользователя в системе и записывает его в $scope
         *
         * */
        this.login = function ($scope,name,email,pass,callback) {
            $http({
                method: 'POST',
                url: "/j_spring_security_check",
                data:  $.param({
                    username: email,
                    password: pass
                }),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data) {
                $scope[name] = data;
                if (plUtils.isFunction(callback)) {
                    callback(data);
                }
            });
        };

        //Класс для настройки фильтров
        function Filter(className,first,max) {
            this.className = className;
            this.first = first;
            this.max = max;

            this.result = {
                first : this.first,
                max : this.max
            };

            /**
             * @param {Number} value - значение на которое произойдет сдвиг в фильтре
             * @return {void}
             * */
            this.increase = function (value) {
                this.result.first = this.result.first + value;
            };

            /**
             * @param {String} fieldName - имя поля по кторому будет идти сортировка
             * @param {Boolean} desc - в обратном порядке или нет
             *
             * @description добавляет в массив сортировку по полю
             *
             * */
            this.setOrder = function(fieldName,desc) {
                if (!("order" in this.result)) {
                    this.result.order = [];
                }

                this.result.order.push({"field" : fieldName , "route" : desc ? "Desc" : "Asc"});
            };

            /**
             * @description Убирает сортировку
             *
             * */
            this.clearOrder = function () {
                this.result.order = [];
            };

            /**
             *
             * @param {Boolean} or - объеденить поиск по 'или'
             * @description создает структуру для поиска строк
             * */
            this.createSearchText = function(or) {
                this.result.searchText = {};
                this.result.searchText.fields = [];
                if (or) {
                    this.result.searchText.or = true;
                }
            };

            /**
             *
             * @param {String} fieldName - имя поля в объекте
             * @param {String} value - значение
             * @description создает структуру для поиска строк
             * */
            this.addSearchField = function(fieldName,value) {
                this.result.searchText.fields.push({fieldName:value});
            };

            /**
             * @description Убирает поиск
             *
             * */
            this.clearSearch = function () {
                this.result.searchText = null;
            };


            /**
             *
             * @param {String} fieldName - имя поля в объекте
             * @param {String} values - значения
             * @description ищет значения среди вбранных
             * */
            this.in = function (fieldName, values) {
                this.result.in = {
                    fieldName : fieldName,
                    values : values
                };
            };

            /**
             * @description Убирает включение
             *
             * */
            this.clearIn = function (fieldName, values) {
                delete this.result.in;
            };

            /**
             *
             * @param {String} fieldName - имя поля в объекте
             * @param {String} value - значение
             * @description ищет значения по равенству
             * */
            this.equal = function (fieldName, value) {
              if (!this.result.equal) {
                  this.result.equal = [];
              }
              this.result.equal.push({
                  fieldName : fieldName,
                  value : value
              });
            };

            /**
             * @description Убирает равенство
             *
             * */
            this.clearEqual = function() {
                delete this.result.equal;
            };

            this.reload = function () {
                this.result.first = 0;
            };


            this.clearAll = function(){
                this.clearOrder();
                this.clearIn();
                this.clearEqual();
                this.clearSearch();
            }
        }

        this.createFilter = function(className,first,max) {
              return new Filter(className,first,max);
        };

        this.filterRequest = function ($scope,name,filter,callback) {
            var isCallbackFunction = plUtils.isFunction(callback);
            $http({
                method: 'POST',
                url: platformDataUrl+'filter',
                data: $.param({className: filter.className,
                    properties : JSON.stringify(filter.result)}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data){
                if (name && data) {
                    $scope[name] = data;
                }
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
            var isCallbackFunction = plUtils.isFunction(callback);
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
            var isCallbackFunction = plUtils.isFunction(callback);

            $http.get(platformDataUrl+"list?className="+className).success(function(data){
                if (name) {
                    $scope[name] = data;
                }
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
            var isCallbackFunction = plUtils.isFunction(callback);

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
            var isCallbackFunction = plUtils.isFunction(callback);
            $http({
                method: 'POST',
                url: platformDataUrl+'action',
                data: $.param({className: className,
                    actionName:actionName,
                    data : JSON.stringify(data)}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data){
                if (name) {
                    $scope[name] = data;
                }
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

        this.createUploader = function ($scope,name,className,actionName,data,callback,prepareItem){
            var isCallbackFunction = plUtils.isFunction(callback);
            var formData = {
                className: className,
                actionName: actionName,
                data: JSON.stringify(data)
            };
            var uploader = new FileUploader({
                url: platformDataUrl+'actionWithFile',
                autoUpload: false,
                onBeforeUploadItem: function(item) {
                    if (plUtils.isFunction(prepareItem)) {
                        prepareItem(formData);
                    }
                    console.log(formData);
                    item.formData.push(formData);
                }
            });

            uploader.onSuccessItem = function(fileItem, response, status, headers) {
                var data = response;
                if (isCallbackFunction){
                    callback(data);
                }
            };
            uploader.onErrorItem = function(fileItem, response, status, headers) {
                errorService.showError(response,status);
            };

            return uploader;

        };

        this.actionWithFile = function ($scope,name,className,actionName,data,files,callback){
            var isCallbackFunction = plUtils.isFunction(callback);
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
                    if (isCallbackFunction){
                        callback(data);
                    }
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
                if (name){
                    $scope[name] = data;
                }
                if (plUtils.isFunction(callback)){
                    callback(data);
                }
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
                if (name){
                    $scope[name] = data;
                }
                if (plUtils.isFunction(callback)){
                    callback(data);
                }
            }).error(function(error, status, headers, config){
                errorService.showError(error,status);
            });
        };

        this.remove = function ($scope,name,className,id,callback) {
            $http.get(platformDataUrl+"remove?className="+className+"&id="+id).success(function(data){
                if (name){
                    $scope[name] = data;
                }
                if (plUtils.isFunction(callback)) {
                    callback(data);
                }
             }).error(function(error, status, headers, config){
                errorService.showError(error,status);
            });
        };

        this.imageHref = function(className,id){
            if (!className || !id) {
                return "";
            }
            return "/data/image?className="+className+"&id="+id;
        };

        this.fileHref = function(className,id,key){
            if (!className || !id) {
                return "";
            }
            return "/data/readFile?className="+className+"&id="+id+"&key="+key;
        };

        this.fileByKeyHref = function(className,id,key){
            if (!className || !id) {
                return "";
            }
            return "/data/readFile?className="+className+"&id="+id+"&key="+key;
        };
    })

    .service("errorService", function (resourceUrl) {
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
        };

    })

    .provider('applicationProvider', function ApplicationServiceProvider() {
        this.$get= ['$http','$stateParams','$sce','pageService','moduleParam','resourceUrl','errorService',function applicationServiceFactory($http,$stateParams,$sce,pageService,moduleParam,resourceUrl,errorService){
            return new applicationService($http,$stateParams,$sce,pageService,moduleParam,resourceUrl,errorService);
        }];

    })

    .config(function ($stateProvider, $urlRouterProvider,$urlMatcherFactoryProvider,applicationServiceProvider,resourceTemplate) {
        var pageInfo = function($http,$stateParams,$sce,pageService,moduleParam,resourceUrl,errorService){
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
                        for (var j = 0; j < moduleParam.length; j++) {
                            if (moduleParam[j] === moduleUrlSplit[i - 1]) {
                                isContains = true;
                                break;
                            }
                        }
                        if (isContains) {
                            continue;
                        }
                        moduleUrl += "/" + moduleUrlSplit[i];
                    }

                    return $http.get(resourceUrl + "page-info/" + language + ".json");
                }).then(function(response) {
                    var data = response.data;
                    application.text = {};
                    for (var stingData in data.text) {
                        application.text[stingData] = $sce.trustAsHtml(data.text[stingData]);
                    }

                    application.language = data.language;

                    if (moduleUrl) {
                        return $http.get(resourceUrl + "module" + moduleUrl + "/page-info/pageInfo.json");
                    } else {
                        return application;
                    }
                }).then(function (response) {
                    if (response === application) {
                        return application;
                    }

                    var data = response.data;
                    for (var key in data) {
                        if (key !== "text") {
                            application[key] = data[key];
                        }
                    }
                    return $http.get(resourceUrl + "module" + moduleUrl + "/page-info/" + language + ".json");
                }).then(function (response) {
                    if (response === application) {
                        return application;
                    }

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

        function getCtrl ($stateParams,$rootScope,pageInfo,moduleParam){
            $rootScope.pageInfo = pageInfo;
            var url = $stateParams.path.split("/");
            for (var i=0; i < moduleParam.length; i++) {
                if (moduleParam[i] === url [url.length - 2]) {
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
        }).state('moduleParam',{
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

        $rootScope.tagPool = [];
    })

    .directive("errorModalTemplate",function(resourceTemplate){
        return{
                restrict: "E",
                scope : {},
                controller: function(errorService,$scope){
                    $scope.$watch(
                        function(){
                            return errorService.getError();
                        },
                        function(newValue,oldValue){
                            if (newValue !== oldValue){
                                $scope.error = newValue;
                            }
                    });
                },
                templateUrl: resourceTemplate+"/error/modalForError.html"
        };
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
                    if (value !== oldValue) {
                        if ($scope.updateValues && plUtils.isFunction($scope.updateValues)){
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
                if (!scope.type){
                    return;
                }
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
    })

    //for textarea
    .directive('insertAtCaret', ['$rootScope', function($rootScope) {
    return {
        link: function(scope, element, attrs) {
            $rootScope.$on('add', function(e, val) {
                console.log('on add');
                console.log(val);
                var domElement = element[0];
                if (document.selection) {
                    domElement.focus();
                    var sel = document.selection.createRange();
                    sel.text = val;
                    domElement.focus();
                } else if (element.selectionStart || element.selectionStart === 0) {
                    var startPos = element.selectionStart;
                    var endPos = element.selectionEnd;
                    var scrollTop = element.scrollTop;
                    domElement.value = element.value.substring(0, startPos) + val + element.value.substring(endPos, element.value.length);
                    domElement.focus();
                    domElement.selectionStart = startPos + val.length;
                    domElement.selectionEnd = startPos + val.length;
                    domElement.scrollTop = scrollTop;
                } else {
                    domElement.value += val;
                    domElement.focus();
                }

            });
        }
    };
}])

    //for div
    .directive('contenteditableKeyListener', ['$rootScope', function($rootScope) {
        return {
            link: function(scope, element, attrs) {
                var domElement = element[0];
                function elementContainsSelection(el) {
                    var sel;
                    if (window.getSelection) {
                        sel = window.getSelection();
                        if (sel.rangeCount > 0) {
                            for (var i = 0; i < sel.rangeCount; ++i) {
                                if (!isOrContains(sel.getRangeAt(i).commonAncestorContainer, el)) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                    return false;
                }

                function isOrContains(node, container) {
                    while (node) {
                        if (node === container) {
                            return true;
                        }
                        node = node.parentNode;
                    }
                    return false;
                }

                $rootScope.$on('add', function(e, val) {
                    console.log('on add');
                    console.log(val);
                    var sel, range;
                    if (window.getSelection) {
                        // IE9 and non-IE
                        sel = window.getSelection();
                        if (elementContainsSelection(domElement)) {
                            if (sel.getRangeAt && sel.rangeCount) {
                                range = sel.getRangeAt(0);
                                range.deleteContents();

                                // Range.createContextualFragment() would be useful here but is
                                // non-standard and not supported in all browsers (IE9, for one)
                                var el = document.createElement("div");
                                el.innerHTML = val;
                                var frag = document.createDocumentFragment(),
                                    node, lastNode;
                                while ((node = el.firstChild)) {
                                    lastNode = frag.appendChild(node);
                                }
                                range.insertNode(frag);

                                // Preserve the selection
                                if (lastNode) {
                                    range = range.cloneRange();
                                    range.setStartAfter(lastNode);
                                    range.collapse(true);
                                    sel.removeAllRanges();
                                    sel.addRange(range);
                                }
                            }
                        }
                    }

                });

                var range;
                element.on("mouseup keyup blur", function(){
                    var doc = domElement.ownerDocument || domElement.document;
                    var win = doc.defaultView || doc.parentWindow;
                    var sel,r;
                    if (win.getSelection) {
                        sel = win.getSelection();
                        if (sel.rangeCount > 0) {
                            var selection = win.getSelection();
                            r = selection.getRangeAt(0);
                            if (isOrContains(r.commonAncestorContainer, domElement)){
                                range = r.cloneRange();
                            }
                        }
                    }
                });

                $rootScope.$on('setCaret', function(e) {
                    domElement.focus();
                        var sel = window.getSelection();
                        if (sel) {
                            if (sel.rangeCount > 0) {
                                sel.removeAllRanges();
                            }
                            var r = document.createRange();
                            if (range){
                                r.setStart(range.startContainer, range.startOffset);
                                r.setEnd(range.endContainer, range.endOffset);
                                r.collapse(true);
                            } else {
                                r.selectNodeContents(domElement);
                                r.collapse(false);
                            }
                            sel.addRange(r);
                        }

                });
            }
        };
    }])
.directive('contenteditable', ['$rootScope','$sce','TagService', function($rootScope,$sce,TagService) {
    return {
        restrict: 'A', // only activate on element attribute
        require: '?ngModel', // get a hold of NgModelController
        link: function(scope, element, attrs, ngModel) {
            if (!ngModel) {
                return;
            } // do nothing if no ng-model
            var el = element[0];
            // Specify how UI should be updated
            ngModel.$render = function() {
                element.html($sce.getTrustedHtml(ngModel.$viewValue || ''));
            };

            // Listen for change events to enable binding
            element.on('blur keyup change', function() {
                scope.$evalAsync(read);
            });
            element.on("keypress", function (event) {
                if (event.which === 13) {
                    scope.$apply(function () {

                    });

                }
            });

            read();

            function read() {
                var html = element.html();
                ngModel.$setViewValue(html);
            }

            ngModel.$parsers.push(function(viewValue) {
                var s = viewValue, result = "";
                var startPos = -1,start = 0,stopPos = -1, j = -1;
                var startText = "";
                while ((startPos = s.indexOf(TagService.startEditable, stopPos + 1)) !== -1 &&
                (j = s.indexOf(TagService.middleEditable, startPos)) !== -1 &&
                (stopPos = s.indexOf(TagService.stopEditable, startPos + 1)) !== -1) {
                    var value = s.substring(startPos + TagService.startEditable.length, j);
                    var index = parseInt(value);
                    if (isNaN(index)) {
                        continue;
                    }

                    var tag = $rootScope.tagPool[index];
                    if (!tag)  {
                        continue;
                    }

                    //var string = angular.toJson(tag.toJson());
                    result += s.substring(start, startPos) + startText + TagService.groupSeparator + tag.toString() + TagService.groupSeparator;
                    startText = "";
                    start = stopPos + TagService.stopEditable.length;
                }
                if (result){
                    result += s.substring(stopPos + TagService.stopEditable.length);
                    return result;
                }
                return s;
            });

            //ngModel.$formatters.push(function(modelValue) {
            //    var value = modelValue.slice(1,modelValue.indexOf("("));
            //    var index = parseInt(value);
            //    return index;
            //});
        }
    };
}])
    .factory("TagService",function(){
        var groupSeparator = String.fromCharCode(29);
        var nonBreakingSpace = "&nbsp;";

        var startEditable = '<span contenteditable="false">';
        var middleEditable = ')_';
        var stopEditable = '</span>';

        var getEditableTag = function(model,tag,index){
            var before = '&#8203;', after = "";
            if (!model){
                after = "</br>";
            }
            return startEditable + (index) + middleEditable + tag.getName() + stopEditable + after;
        };

        return{
            groupSeparator:groupSeparator,
            nonBreakingSpace:nonBreakingSpace,

            getEditableTag:getEditableTag,
            startEditable: startEditable,
            middleEditable: middleEditable,
            stopEditable: stopEditable

        };
    });


function Tag() {
    this.Type = Object.freeze({Program: 1, Math: 2, Image: 3});

    var name = "tag";
    var type = this.Type.Program;
    var data = {};

    this.getName = function() {
        return name;
    };

    this.setName = function(n){
        if (!name || typeof name !== 'string') {
            console.error("Tag name is not a valid");
            return;
        }
        name = n;
    };

    this.getType = function() {
        return type;
    };

    this.setType = function(t){
        if (!t) {
            console.error("Tag type is not a valid");
            return;
        }
        type = t;
    };

    this.getData = function() {
        return data;
    };

    this.setData = function(d) {
        if (!d || d === null || typeof d !== 'object') {
            console.error("Tag data is not a valid");
            return;
        }
        data = d;
    };

    this.toJson = function(){
        //var json =
        //angular.merge(json,data);
        return {name:this.getName()};
    };

    this.toString = function(){
        return angular.toJson(this.toJson());
    };
}

function ProgramTag() {
    Tag.call(this);

    var options = {
        lineNumbers: true,
        indentWithTabs: true
    };

    var code = "";

    this.getCode = function() {
        return code;
    };

    this.setCode = function(t) {
        if (!t || !angular.isString(t)) {
            console.error("Tag text is not a valid");
            return;
        }
        code = t;
    };

    this.setMode = function(m){
        options.mode = m;
    };

    this.setTheme = function(t){
        options.theme = t;
    };

    this.getOptions = function(){
        return angular.copy(options);
    };

    this.getReadOnlyOptions = function(){
        return angular.extend({
            readOnly: 'nocursor'
        }, options);
    };


    var parentJson = this.toJson;
    this.toJson = function(){
        var json = parentJson.call(this);
        angular.merge(json,{code:code, options:this.getOptions()});
        return json;
    };
}

