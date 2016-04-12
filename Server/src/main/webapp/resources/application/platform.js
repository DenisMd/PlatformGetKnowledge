String.prototype.capitalizeFirstLetter = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
};


function PlatformUtils(){
    this.isFunction = function(func){
        return !!(func && angular.isFunction(func));

    };
    this.valueToString = function(val){
        return val !== null ? val.toString() : val;
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
            "tutorial" : "com.getknowledge.modules.courses.tutorial.Tutorial",
            "news" : "com.getknowledge.modules.news.News"
         };
    })
    .factory('moduleParam',function(){
        //Параметризированные модули(получают параметры из url)
        return ["user","accept","section","restorePassword","groupCourses","groupBooks","groupPrograms","book","program","course","tutorial"];
    })
    .provider('$languages',function(){
        return {
            defaultLanguage : "ru",
            languages : ["ru","en"],
            $get: function(){
                return {
                    defaultLanguage : this.defaultLanguage,
                    languages : this.languages
                }
            }
        }
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
            if (!key) {
                return "";
            }

            if (!url) {
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
    })

    .service("applicationService", function ($http,$stateParams,$sce,FileUploader,pageService,moduleParam,resourceUrl,platformDataUrl,errorService) {

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
            };
        }

        /**
         *
         * @param {String} className - имя класса для которого создается фильтр
         * @param {Number} first - с какого номера начинать поиск
         * @param {Number} max - сколько элементов должно быть в ответе
         *
         * @return {Filter}
         * */
        this.createFilter = function(className,first,max) {
              return new Filter(className,first,max);
        };


        /**
         *  @param {Object} $scope - скопе из которого вызывается метод
         *  @param {String} name - имя в скопе в которое запишется response
         *  @param {Filter} filter - созданный фильтр по которому будет построен запрос
         *  @param {Function} callback - функция пост-обработки response
         *
         *  @description создает запрос для фильтра объектов
         * */
        this.filterRequest = function ($scope,name,filter,callback) {

            var isCallbackFunction = plUtils.isFunction(callback);

            if (filter === null || filter.result === null) {
                console.error("Filter is null in filterRequest");
                return;
            }

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
                    if (angular.isArray(data.list)) {
                        data.list.forEach(function(item,i,array){
                            callback(item,i,array,data.creatable);
                        });
                    }
                }
            }).error(function(error, status){
                errorService.showError(error,status);
            });
        };

        /**
         *  @param {Object} $scope - скопе из которого вызывается метод
         *  @param {String} name - имя в скопе в которое запишется response
         *  @param {String} className - имя класса
         *  @param {Integer} id - 8-byte идентификатор сущности
         *  @param {Function} callback - функция пост-обработки response
         *
         * @description Читает объект по его идентификатору и записывает его в $scope[name]
         * */
        this.read = function($scope, name, className, id, callback) {
            var isCallbackFunction = plUtils.isFunction(callback);
            $http.get(platformDataUrl+"read?className="+className+"&id="+id)
                .success(function(data){
                    if (name) {
                        $scope[name] = data;
                    }
                    if (isCallbackFunction) {
                        callback(data);
                    }
                }).error(function(error, status, headers, config){
                    errorService.showError(error,status);
                });

        };

        /**
         *  @param {Object} $scope - скопе из которого вызывается метод
         *  @param {String} name - имя в скопе в которое запишется response
         *  @param {String} className - имя класса
         *
         *
         * @description Записывает кол-во объектов в $scope[name]
         * */
        this.count = function($scope, name, className) {
            $http.get(platformDataUrl+"count?className="+className).success(function(data){
                $scope[name] = data;
            }).error(function(error, status){
                errorService.showError(error,status);
            });
        };


        /**
         *  @param {Object} $scope - скопе из которого вызывается метод
         *  @param {String} name - имя в скопе в которое запишется response
         *  @param {String} className - имя класса
         *  @param {Function} callback - функция пост-обработки response
         *
         * @description Читает весь список объектов и записывает его в $scope[name]
         * */
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
            }).error(function(error, status){
                errorService.showError(error,status);
            });
        };

        /**
         *  @param {Object} $scope - скопе из которого вызывается метод
         *  @param {String} name - имя в скопе в которое запишется response
         *  @param {String} className - имя класса
         *  @param {Integer} first - номер с кторого нужно начинать
         *  @param {Integer} max - окно(ко-во) элементов в запросе
         *  @param {Function} callback - функция пост-обработки response
         *
         * @description Читает частичный список и записывает его в $scope[name]
         * */
        this.listPartial = function ($scope,name,className,first,max,callback) {
            var isCallbackFunction = plUtils.isFunction(callback);

            $http.get(platformDataUrl+"listPartial?className="+className+"&first="+first+"&max="+max).success(function(data){

                if (name) {
                    $scope[name] = data;
                }

                if (isCallbackFunction){
                    data.forEach(function(item,i,array){
                        callback(item,i,array);
                    });
                }
            }).error(function(error, status){
                errorService.showError(error,status);
            });
        };

        /**
         *  @param {Object} $scope - скопе из которого вызывается метод
         *  @param {String} name - имя в скопе в которое запишется response
         *  @param {String} className - имя класса
         *  @param {String} actionName - имя действия на сторне сервера
         *  @param {Object} data - данные передаваемые на сервер
         *  @param {Function} callback - функция пост-обработки response
         *
         * @description выполняет определенную логику на сервере и результат записывает в $scope[name]
         * */
        this.action = function ($scope,name,className,actionName,data,callback){
            var isCallbackFunction = plUtils.isFunction(callback);
            $http({
                method: 'POST',
                url: platformDataUrl+'action',
                data: $.param({
                    className: className,
                    actionName:actionName,
                    data : JSON.stringify(data)
                }),
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

        /**
         *  @param {Object} $scope - скопе из которого вызывается метод
         *  @param {String} name - имя в скопе в которое запишется response
         *  @param {String} className - имя класса
         *  @param {String} actionName - имя действия на сторне сервера
         *  @param {Object} data - данные передаваемые на сервер
         *  @param {Function} callback - функция пост-обработки response
         *  @param {Function} prepareItem - функцей вызывается перед загрузкой элементов на сервер
         *
         *  @return {FileUploader}
         * @description создает uploader
         * */
        this.createUploader = function ($scope,name,className,actionName,data,callback,prepareItem){
            var isCallbackFunction = plUtils.isFunction(callback);
            var formData = {
                className:  className,
                actionName: actionName,
                data:       JSON.stringify(data)
            };
            var uploader = new FileUploader({
                url: platformDataUrl+'actionWithFile',
                autoUpload: false,
                onBeforeUploadItem: function(item) {
                    if (plUtils.isFunction(prepareItem)) {
                        prepareItem(formData);
                    }

                    item.formData.push(formData);
                }
            });

            uploader.onSuccessItem = function(fileItem, response) {
                if (isCallbackFunction){
                    callback(response);
                }
            };
            uploader.onErrorItem = function(fileItem, response, status) {
                errorService.showError(response,status);
            };

            return uploader;

        };


        /**
         *  @param {Object} $scope - скопе из которого вызывается метод
         *  @param {String} name - имя в скопе в которое запишется response
         *  @param {String} className - имя класса
         *  @param {String} actionName - имя действия на сторне сервера
         *  @param {Object} data - данные передаваемые на сервер
         *  @param {Object} files - файлы
         *  @param {Function} callback - функция пост-обработки response
         *
         * @description загружает файлы на сервер
         * */
        this.actionWithFile = function ($scope,name,className,actionName,data,files,callback){
            var isCallbackFunction = plUtils.isFunction(callback);
            var formData = {
                className:  className,
                actionName: actionName,
                data:       JSON.stringify(data)
            };
            if (files) {
                var uploader = this.createUploader($scope,name,className,actionName,data,callback,null);

                if (Array.isArray(files)){
                    uploader.queue = files;
                } else {
                    uploader.addToQueue(files);
                }
                uploader.uploadAll();
            }
        };

        /**
         *  @param {Object} $scope - скопе из которого вызывается метод
         *  @param {String} name - имя в скопе в которое запишется response
         *  @param {String} className - имя класса
         *  @param {Object} data - модель объекта         *
         *  @param {Function} callback - функция пост-обработки response
         *
         * @description создает объект из модели
         * */
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

        /**
         *  @param {Object} $scope - скопе из которого вызывается метод
         *  @param {String} name - имя в скопе в которое запишется response
         *  @param {String} className - имя класса
         *  @param {Object} data - модель объекта         *
         *  @param {Function} callback - функция пост-обработки response
         *
         * @description обновляет данные модели
         * */
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

        /**
         *  @param {Object} $scope - скопе из которого вызывается метод
         *  @param {String} name - имя в скопе в которое запишется response
         *  @param {String} className - имя класса
         *  @param {Integer} id - ид сущности         *
         *  @param {Function} callback - функция пост-обработки response
         *
         * @description удаляет данные по id
         * */
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

        /**
         *  @param {String} className - имя класса
         *  @param {Integer} id - ид сущности
         *
         * @description получает ссылку для показа изображения
         * */
        this.imageHref = function(className,id){
            if (!className || !id) {
                return "";
            }
            return "/data/image?className="+className+"&id="+id;
        };

        /**
         *  @param {String} className - имя класса
         *  @param {Integer} id - ид сущности
         *  @param {String} key - ключ по которому будет получен файл
         *
         * @description получает ссылку для скачивания файла
         * */
        this.fileByKeyHref = function(className,id,key){
            if (!className || !id) {
                return "";
            }
            return "/data/readFile?className="+className+"&id="+id+"&key="+key;
        };
    })

    .service("errorService", function () {
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

    .config(function ($stateProvider,$urlRouterProvider,$urlMatcherFactoryProvider,$languagesProvider,resourceTemplate) {
        var applicationProperties = function($http,$state,$stateParams,$languages,$timeout,$sce,pageService,moduleParam,resourceUrl){
            var applicationData;
            var moduleUrl = "";
            var language = $stateParams.language ? $stateParams.language : pageService.getLanguage();

            if ($languages.languages.indexOf(language) === -1) {
                //Невозможно сделать переход из resolve : {};
                $timeout(function(){
                    if (!$stateParams.path) {
                        $state.go("home", {language: $languages.defaultLanguage});
                    } else {
                        $state.go("moduleParam", {language: $languages.defaultLanguage, path : $stateParams.path});
                    }
                });
                return;
            }

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

                    if (response === undefined) {
                        return;
                    }

                    //Так как может вернуться applicationData
                    if (response === applicationData) {
                        return applicationData;
                    }

                    var data = response.data;
                    for (var key in data) {
                        if (key !== "text") {
                            applicationData[key] = data[key];
                        }
                    }

                    //Загружаем переводы модуля
                    return $http.get(resourceUrl + "module" + moduleUrl + "/page-info/" + language + ".json");
                }, function(error) {
                    console.error("Error loading application properties (" + error.config.url + ")");
                    return $http.get(resourceUrl + "module" + moduleUrl + "/page-info/" + language + ".json");
                }).then(function (response) {

                    if (response === undefined) {
                        return;
                    }

                    if (response === applicationData) {
                        return applicationData;
                    }

                    var data = response.data;
                    for (var key in data.text) {
                        if (applicationData.text[key]) {
                            continue;
                        }
                        applicationData.text[key] = data.text[key];
                    }
                    return applicationData;
                }, function(error) {
                    console.error("Error loading page translation(" + error.config.url + ")");
                });
        };
        $urlMatcherFactoryProvider.type('nonURIEncoded', {
            encode: plUtils.valueToString,
            decode: plUtils.valueToString,
            is: function () { return true; }
        });

        function getURL ($stateParams){
            return "module/" + $stateParams.path;
        }

        function getCtrl ($stateParams,$rootScope,applicationProperties,moduleParam){
            $rootScope.application = applicationProperties;
            var url = $stateParams.path.split("/");
            for (var i=0; i < moduleParam.length; i++) {
                if (moduleParam[i] === url [url.length - 2]) {
                    return url [url.length - 2] + "Ctrl";
                }
            }
            return url [url.length - 1] + "Ctrl";
        }

        $urlRouterProvider.when('' , '/' + $languagesProvider.defaultLanguages);
        $urlRouterProvider.when('/' , '/' + $languagesProvider.defaultLanguages);
        $urlRouterProvider.when('/#' , '/' + $languagesProvider.defaultLanguages);
        $urlRouterProvider.when('/#/' , '/' + $languagesProvider.defaultLanguages);

        $stateProvider.state('home', {
            url : "/:language",
            resolve: {
                applicationProperties : applicationProperties
            },
            views : {
                '' : {
                    templateUrl : resourceTemplate + 'indexTemplate.html',
                    controllerProvider : function($rootScope,applicationProperties){
                        $rootScope.application = applicationProperties;
                        return "indexController";
                    }
                }
            }
        }).state('moduleParam',{
            url : '/:language/{path:nonURIEncoded}',
            resolve: {
                applicationProperties : applicationProperties
            },
            views : {
                '' : {
                    templateUrl : getURL,
                    controllerProvider : getCtrl

                }
            }
        })  .state("404",{
            resolve: {
                applicationProperties : applicationProperties
            },
            templateUrl: "/404",
            controller : function($rootScope,applicationProperties){
                $rootScope.application = applicationProperties;
            }
        })
            .state("accessDenied",{

                resolve: {
                    applicationProperties : applicationProperties
                },
                templateUrl: "/accessDenied",
                controller : function($rootScope,applicationProperties, $scope){
                    $rootScope.application = applicationProperties;
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
                        function(){
                            return errorService.getError();
                        },
                        function(newValue,oldValue){
                            if (newValue !== oldValue){
                                $scope.error = newValue;
                            }
                    });
                },
                templateUrl: resourceTemplate+"/error/errorDialog.html"
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
    });


