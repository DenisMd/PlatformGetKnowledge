new Clipboard('.btn');

var model = angular.module("mainApp", ["BackEndService", "ui.bootstrap", "ngImgCrop" , "ngMaterial","chart.js"]);

var player;

function initVideoPlayer() {
    var options = {
        "controls": true,
        "preload": "matadata",
        "autoplay": false,
        "width": 720,
        "height": 480
    };
    player = videojs(document.getElementById('main-video'), options, function () {
        player = this;
    });

}

model.controller("mainController", function ($scope,$rootScope, $http, $state, applicationService,pageService, className,$mdToast,$mdDialog, $mdMedia) {

    //Toast
    $scope.showToast = function (text) {
        $mdToast.show(
            $mdToast.simple()
                .textContent($scope.translate(text))
                .position("bottom right")
                .hideDelay(3000)
        );
    };

    //Dialog
    $scope.showDialog = function (ev,$scope,htmlName,callbackForOk) {
        var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'))  && $scope.customFullscreen;
        $mdDialog.show({
                controller: DialogController,
                templateUrl: htmlName,
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose:true,
                fullscreen: useFullScreen,
                locals: {
                    theScope: $scope
                }
            })
            .then(function(answer) {
                callbackForOk(answer);
            });
        $scope.$watch(function() {
            return $mdMedia('xs') || $mdMedia('sm');
        }, function(wantsFullScreen) {
            $scope.customFullscreen = (wantsFullScreen === true);
        });
    };

    //Устанавливает сортировку для массива
    var reverse = false;
    $scope.setOrder = function (order) {
        reverse = !reverse;
        $scope.order = reverse?"-"+order:order;
    };


    //---------------------------------------- системные методы
    //Получаем url для загрузки видео
    $scope.getVideoUrl = function (id) {
        return "/data/readVideo?className="+className.video+"&id="+id;
    };

    //перевести по ключу
    $scope.translate = function (key) {
        if (!$scope.application || !$scope.application.text || !(key in $scope.application.text)) {
            return key;
        }

        return $scope.application.text[key];

    };

    //создать ссылку на страницу с учетом языка
    $scope.createUrl = function (url) {
        if (!$scope.application) return;
        return '#/' + $scope.application.language + url;
    };

    //смена языка
    $scope.changeLanguage = function (language) {
        if (!$scope.application.language || $scope.application.language === language) {
            return false;
        }
        if ($state.includes('404') || $state.includes('accessDenied')){
            $rootScope.application = pageInfo;
            return true;
        } else {
            var str = window.location.hash.split("/").splice(2).join("/");
            if (str) {
                $state.go("modules", {
                    language: language,
                    path: str
                });
            } else {
                $state.go("home", {
                    language: language
                });
            }
            return true;
        }
    };

    //создает массив для ng-repeat
    $scope.range = function(n) {
        if (!n) return 1;
        return new Array(Math.ceil(n));
    };

    //создаем массив по диапозону
    $scope.getRow = function (index, length, array) {
        var result = [];
        for (var i = index*length; i < length*(index+1); i++) {
            if (array.length <= i) return result;
            result.push(array[i]);
        }
        return result;
    };

    //---------------------------------------- методы для меню
    //Разлогиниваемся
    $scope.logout = function(){
        if (!$scope.user) return;
        $http.get("/j_spring_security_logout").success(function(){
            applicationService.action($scope, "user", className.userInfo, "getAuthorizedUser", {},function(){
                $scope.reloadMenu();
                pageService.onLogout();
            });
        });
    };

    $scope.toggelMenu = true;

    $scope.toggelClick = function () {
        $scope.toggelMenu = !$scope.toggelMenu;
        var wrapper = angular.element("#wrapper");
        wrapper.toggleClass("wrapper-left");
    };

    $scope.menuScrollConfig = {
        theme: 'light-3',
        snapOffset: 100,
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: "ul li"
        }
    };

    //scroll для модалок
    $scope.modalScrollConfig = {
        theme: 'dark-3',
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: true
        }
    };

    $scope.userImg = function(id){
        return applicationService.imageHref(className.userInfo,id);
    };

    //--------------------------------------------- опции слайдера
    $scope.carouselData = {
        interval : 5000,
        slides : [
            {
                section: "Programming",
                image: "/resources/image/index/slider/programming.jpg",
                text: "carousel_programming"
            },
            {
                section: "Math",
                image: "/resources/image/index/slider/math.jpg",
                text: "carousel_math"
            },
            {
                section: "Physic",
                image: "/resources/image/index/slider/physic.jpg",
                text: "carousel_physic"
            },
            {
                section: "Design",
                image: '/resources/image/index/slider/design.jpg',
                text: "carousel_design"
            }
        ]
    };

    $scope.reloadMenu = function(callback){
        applicationService.action($scope, "menu", className.menu, "getMenu", {}, function(menu){
            if (angular.isFunction(callback)){
                callback(menu);
            }
        });
    };
    $scope.reloadMenu(function(menu){
        $scope.cardsData = {
            title : "ourCourses",
            cardsInRow : 3,
            cards : menu.items,
            prefix : ''
        };
    });

    applicationService.action($scope, "user", className.userInfo, "getAuthorizedUser", {});

    applicationService.action($scope , "countries" , "com.getknowledge.modules.dictionaries.country.Country" , "getCountries",{
        language : "Ru"
    });
});

model.controller("videoCtrl",function($scope){
    initVideoPlayer();

    var videoUrl = $scope.getVideoUrl(1);
    $scope.url = {type: "video/mp4", src: videoUrl};

    $scope.open = function() {
        if (!player ||player.currentSrc() != $scope.url.src) {
            player.src($scope.url);
            player.play();
        } else {
            player.play();
        }
        $('#videoModal').modal('show');

    };

    $scope.close = function(){
        if (player) {
            player.pause();
        }
    };

    $('#videoModal').on("hidden.bs.modal",function(){
        $scope.close();
    });
});

model.controller("textPlainCtrl" , function($scope,$uibModal) {
    $scope.open = function (size , item) {

        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'textPlaneContent.html',
            controller: 'panelModalCtrl',
            size: size,
            resolve: {
                item: function () {
                    return item;
                },
                parentScope : function () {
                    return $scope;
                },
                callbackForClose: function(){
                    return;
                }
            }
        });
    }
});

//select value
model.controller("inputCtrl",function($scope,$sce,$filter,$document) {
    $scope.choose = false;
    var isModelOpen = false;
    $scope.model =  $scope.getData().defaultValue in $scope ? $scope[$scope.getData().defaultValue] : "";
    $scope.modalModel;
    $scope.selectModalValue;
    $scope.selectValue;

    $scope.filter = $scope.getData().filter;
    $scope.id = $scope.getData().id;
    $scope.count = $scope.getData().count;
    $scope.class = $scope.getData().class;
    $scope.callback = angular.isFunction($scope.getData().callback)? $scope.getData().callback : null;
    $scope.list = [];
    var selector = '#' + $scope.id;

    $scope.closeModal = function(){
        $(selector).modal("hide");
        $scope.resetActiveElementInModal();
        $scope.modalModel = "";
        $scope.selectModalValue = null;
        isModelOpen = false;
    } ;
    $scope.getItem = function (item) {
        if (!item) return "";
        if (item.$$unwrapTrustedValue) {
            return item;
        } else {
            return item[$scope.filter];
        }
    };

    $scope.getList = function(){
        $scope.list =  $scope.getData().listName in $scope ? $scope[$scope.getData().listName] : [];
        return $scope.list;
    };

    $scope.getFilteredData = function (isModal) {
        var list = $scope.getList();

        var filter = {};
        if (!$scope.filter) {
            filter = isModal?$scope.modalModel:$scope.model;
        } else {
            filter[$scope.filter] = isModal?$scope.modalModel:$scope.model;
        }
        var filteredData = $filter('filter')(list,filter);
        if (!isModal) {
            filteredData = $filter('limitTo')(filteredData, $scope.count);

            if (filteredData) {
                var valid = true;

                if (filteredData.length === 1) {
                    if ($scope.choose && $scope.model.toString() === $scope.getItem(filteredData[0]).toString()) {
                        $scope.setModel(filteredData[0]);
                    }
                } else {
                    if (filteredData.length === 0) {
                        if ($scope.isRequired()) {
                            valid = false;
                        }
                    }
                }
            }
            if (!$scope.choose && $scope.model && !($scope.model.toString() === $scope.getItem(filteredData[0]).toString())){
                valid = false;
            }
            $scope.selectForm['main-select'].$setValidity("selectValue", valid);
            $scope.setValid();
        } else {
            if (isModelOpen) {
                if (filteredData.length === 0) {
                    $scope.selectForm['search-input'].$setValidity("searchValue", false);
                } else {
                    $scope.selectForm['search-input'].$setValidity("searchValue", true);
                }
            }
        }
        return filteredData;
    };

    $scope.empty = function(isModal){
          return $scope.getFilteredData(isModal).length === 0;
    };

    $scope.setSelect = function (value) {
        $scope.choose = value;
    };

    $scope.onEvent = function (event) {
        var elem = angular.element(event.currentTarget);
        switch (event.type) {
            case "mouseenter":
                elem.addClass("active");
                break;
            case "mouseleave":
                elem.removeClass("active");
                break;
        }
    };

    $scope.resetModel = function () {
        $scope.model = "";
        $scope.selectValue = null;
        $scope.choose = false;
    };

    $scope.setModel = function (value) {
        $scope.model = getValue(value);
        $scope.selectValue = value;
        $scope.choose = false;
        $scope.callback(value);
    };

    $scope.open = function () {
        $(selector).modal({
            backdrop: 'static',
            keyboard: false
        });
        $(selector).modal('show');
        $(selector+" .table-content").height(getHeight());
        isModelOpen = true;
    };

    var currentElement;
    $scope.resetActiveElementInModal = function(){
        if (currentElement) {
            currentElement.removeClass("info");
            currentElement = null;
        }
    };

    $scope.setModalModel = function(event,value){
        $scope.resetActiveElementInModal();
        var elem = angular.element(event.currentTarget);
        currentElement = elem;
        elem.addClass("info");
        $scope.selectModalValue = value;
    };

    $scope.saveModalModel = function(){
        $('#' + $scope.id).modal('hide');
        $scope.setModel($scope.selectModalValue);
        $scope.resetActiveElementInModal();
    };

    //закрытие подсказки, если щелчок происходдит за пределами элемнта
    $scope.hideSelect = function(){
        $scope.$apply(function () {
            if ($scope.choose) {
                $scope.getFilteredData();
                $scope.choose = false;
            }
        });
    };

    $scope.isRequired = function(){
        var val = $scope.getData().required;

        if (!val) return false;

        if (angular.isFunction(val)){
            return val();
        } else{
            return val;
        }
    };

    //есть ли запрет на редактирование
    $scope.isDisabled = function(){
        var val = $scope.getData().disable;

        if (!val) return false;

        if (angular.isFunction(val)){
            return val();
        } else{
            return val;
        }
    };

    $scope.setValid = function(){
        var val = $scope.getData().isValid;

        if (!val) return false;
        if (angular.isFunction(val)) {
            val($scope.selectForm['main-select'].$valid);
        }
    };

    //scroll для таблицы
    $scope.selectScrollConfig = angular.merge({setHeight: getHeight()}, $scope.modalScrollConfig);

    //ожидание сброса значения
    $scope.$on('reset'+$scope.id.capitalizeFirstLetter()+'Event', function(event, args) {
        $scope.resetModel();
    });

    //подсчет высоты основного содержания модалки
    function getHeight(){
        var height = $scope.getData().maxHeight? $scope.getData().maxHeight: 400;
        var temp = 40 * $scope.getList().length;
        return !temp || temp > height? height : temp;
    }

    //текст отображающийся в input
    function getValue(value){
        if (angular.isString(value) || value.$$unwrapTrustedValue) {
            return value;
        } else {
            return value[$scope.filter];
        }
    }

});

model.directive("hideOptions",function($document){
    return {
        restrict: 'A',
        scope:{
            "callback":"&hideOptions"
        },
        link: function (scope, element, attr) {
            var input = element.find("input")[0];
            var button = element.find("button")[0];
            if (scope.callback && angular.isFunction(scope.callback)){
                $document.on("click", function (event) {
                    switch (event.target){
                        case input:
                        case button:
                            return;
                    }

                    scope.$apply(scope.callback());

                });
            }
        }
    };
});

//crop image
model.controller("selectImgCtrl", function($scope,$uibModal){

    var open = function (size , item , callBackForClose) {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'cropModal.html',
            controller: 'panelModalCtrl',
            size: size,
            resolve: {
                item: function () {
                    return item;
                },
                callbackForClose: function() {
                    return callBackForClose;
                }
                ,
                parentScope : function () {
                    return $scope;
                }
            }
        });
    };

    $scope.isInModal = $scope.getData().isInModal? $scope.getData().isInModal : false;
    $scope.originalImg='';
    $scope.croppedImg='';

    //$($scope.id).modal({
    //    backdrop: 'static',
    //    keyboard: false
    //});

    $scope.uploadFile = function(file) {
        if (file) {
            // ng-img-crop
            var imageReader = new FileReader();
            imageReader.onload = function(image) {
                if ($scope.isInModal) {
                    initModalImage(image);
                } else {
                    $scope.$apply(function($scope) {
                        $scope.originalImg = image.target.result;
                    });

                }
            };
            imageReader.readAsDataURL(file);
        }
    };


    $scope.getAreaType = function(){
        if ($scope.getData().isSquare){
            return "square";
        }
        return "circle";
    };

    $scope.onChange = function (element) {
        console.log(element);
        if (angular.isFunction($scope.getData().save)) {
            var file = base64ToBlob(element.replace('data:image/png;base64,',''), 'image/jpeg');
            //DwENAIJ4HAYF4AwSagD9IczM1IiCQkUNbswkIpLmZGhEQyMihrdkEBNLcTI0ICGTk0NZsAgJpbqZGBAQycmhrNgGBNDdTIwICGTm0NZuAQJqbqREBgYwc2ppNQCDNzdSIgEBGDm3NJiCQ5mZqREAgI4e2ZhMQSHMzNSIgkJFDW7MJCKS5mRoREMjIoa3ZBATS3EyNCAhk5NDWbAICaW6mRgQEMnJoazYBgTQ3UyMCAhk5tDWbgECam6kRAYGMHNqaTUAgzc3UiIBARg5tzSYgkOZmakRAICOHtmYTEEhzMzUiIJCRQ1uzCQikuZkaERDIyKGt2QQE0txMjQgIZOTQ1mwCAmlupkYEBDJyaGs2AYE0N1MjAgIZObQ1m4BAmpupEQGBjBzamk1AIM3N1IiAQEYObc0mIJDmZmpEQCAjh7ZmExBIczM1IiCQkUNbswkIpLmZGhEQyMihrdkEBNLcTI0ICGTk0NZsAgJpbqZGBAQycmhrNgGBNDdTIwICGTm0NZuAQJqbqREBgYwc2ppNQCDNzdSIgEBGDm3NJiCQ5mZqREAgI4e2ZhMQSHMzNSIgkJFDW7MJCKS5mRoREMjIoa3ZBATS3EyNCAhk5NDWbAICaW6mRgQEMnJoazYBgTQ3UyMCAhk5tDWbgECam6kRAYGMHNqaTUAgzc3UiIBARg5tzSYgkOZmakRAICOHtmYTEEhzMzUiIJCRQ1uzCQikuZkaERDIyKGt2QQE0txMjQgIZOTQ1mwCAmlupkYEBDJyaGs2AYE0N1MjAgIZObQ1m4BAmpupEQGBjBzamk1AIM3N1IiAQEYObc0mIJDmZmpEQCAjh7ZmExBIczM1IiCQkUNbswkIpLmZGhEQyMihrdkEBNLcTI0ICGTk0NZsAgJpbqZGBAQycmhrNgGBNDdTIwICGTm0NZuAQJqbqREBgYwc2ppNQCDNzdSIgEBGDm3NJiCQ5mZqREAgI4e2ZhMQSHMzNSIgkJFDW7MJCKS5mRoREMjIoa3ZBATS3EyNCAhk5NDWbAICaW6mRgQEMnJoazYBgTQ3UyMCAhk5tDWbgECam6kRAYGMHNqaTUAgzc3UiIBARg5tzSYgkOZmakRAICOHtmYTEEhzMzUiIJCRQ1uzCQikuZkaERDIyKGt2QQE0txMjQgIZOTQ1mwCAmlupkYEBDJyaGs2AYE0N1MjAgIZObQ1m4BAmpupEQGBjBzamk1AIM3N1IiAQEYObc0mIJDmZmpEQCAjh7ZmExBIczM1IiCQkUNbswkIpLmZGhEQyMihrdkEBNLcTI0ICGTk0NZsAgJpbqZGBAQycmhrNgGBNDdTIwICGTm0NZuAQJqbqREBgYwc2ppNQCDNzdSIgEBGDm3NJiCQ5mZqREAgI4e2ZhMQSHMzNSIgkJFDW7MJCKS5mRoREMjIoa3ZBATS3EyNCAhk5NDWbAICaW6mRgQEMnJoazYBgTQ3UyMCAhk5tDWbgECam6kRAYGMHNqaTUAgzc3UiIBARg5tzSYgkOZmakRAICOHtmYTEEhzMzUiIJCRQ1uzCQikuZkaERDIyKGt2QQE0txMjQgIZOTQ1mwCAmlupkYEBDJyaGs2AYE0N1MjAgIZObQ1m4BAmpupEQGBjBzamk1AIM3N1IiAQEYObc0mIJDmZmpEQCAjh7ZmExBIczM1IiCQkUNbswkIpLmZGhH4AStUAMmSuOW2AAAAAElFTkSuQmCC
            $scope.getData().save(file);
        }
    };

    function initModalImage(image){
        $scope.$apply(function($scope) {
            $scope.originalImg = image.target.result;
            var item = {
                original : $scope.originalImg,
                cropImage:  $scope.croppedImg
            };
            open('lg',item,save);
        });
    }

    function base64ToBlob(base64Data, contentType) {
        contentType = contentType || '';
        var sliceSize = 1024;
        var byteCharacters = atob(base64Data);
        var bytesLength = byteCharacters.length;
        var slicesCount = Math.ceil(bytesLength / sliceSize);
        var byteArrays = new Array(slicesCount);

        for (var sliceIndex = 0; sliceIndex < slicesCount; ++sliceIndex) {
            var begin = sliceIndex * sliceSize;
            var end = Math.min(begin + sliceSize, bytesLength);

            var bytes = new Array(end - begin);
            for (var offset = begin, i = 0 ; offset < end; ++i, ++offset) {
                bytes[i] = byteCharacters[offset].charCodeAt(0);
            }
            byteArrays[sliceIndex] = new Uint8Array(bytes);
        }
        return new Blob(byteArrays, { type: contentType });
    };
});

//datapicker
model.controller("datepickerCtrl", function($scope){
    init();
    var defaultFormat = "dd.MM.yyyy";
    $scope.popupOptions = {
        "current" : $scope.translate("datapiker-today"),
        "clear" : $scope.translate("datapiker-clear"),
        "close" : $scope.translate("datapiker-close")
    };

    $scope.getFormat = function(){
        if ($scope.format){
            return $scope.format;
        }
        return defaultFormat;
    };

    $scope.change = function(){
        if (angular.isFunction($scope.onChange)){
            $scope.onChange($scope.date);
        }
    };

    $scope.status = {
        opened: false
    };
    $scope.date = null;

    $scope.open = function($event) {
        $scope.status.opened = true;
    };

    $scope.$watch("date", function(newVal,oldVal){
        if (newVal != oldVal){
            $scope.change();
        }
    });

    $scope.updateValues = function(){
        init();
    };

    function init() {
        var options = null;
        if ($scope.getData()){
            $scope.required = $scope.getData().required;
            $scope.onChange = $scope.getData().onChange;
            $scope.format = $scope.getData().format;
            options = $scope.getData().options;
        }
        if (options){
            $scope.dataOptions = options;
            $scope.dataOptions["show-weeks"] = $scope.dataOptions["show-weeks"]? true:false;
        } else {
            $scope.dataOptions = {
                "show-weeks": false
            };
        }
    }
});

model.directive('datepickerPopupFormat',function(dateFilter,$parse){
    return{
        restrict:'A',
        require:'?ngModel',
        link:function(scope,element,attrs,ngModel,ctrl){
            ngModel.$parsers.push(function(viewValue){
                 return dateFilter(viewValue,attrs.uibDatepickerPopup);
            });
        }
    }
});

model.controller("textareaCtrl",function($scope,$element){
    var textarea = $element.find('textarea');
    $scope.showingTextarea = false;
    $scope.showEditButton = false;
    $scope.model = {};

    $scope.toggelEditButton = function(event){
        if ($scope.isReadonly()) return;

        var elem = angular.element(event.currentTarget);
        switch (event.type) {
            case "mouseenter":
                $scope.showEditButton = true;
                if (!$scope.showingTextarea){
                    elem.addClass("edit-text");
                } else {
                    elem.removeClass("edit-text");
                }
                break;
            case "mouseleave":
                $scope.showEditButton = false;
                elem.removeClass("edit-text");
                break;
        }
    };

    $scope.showTextarea = function(event){
        var elem = null;

        if (event){
            elem = angular.element(event.currentTarget).parent().parent();
        }

        $scope.showingTextarea = !$scope.showingTextarea;
        if ($scope.showingTextarea){
            elem.removeClass("edit-text");
            $scope.model.text = $scope.getData().text;
            textarea.focus();
        }
    };

    $scope.save = function(){
        if (!$scope.model.text) return;
        if(angular.isFunction($scope.getData().onSave)){
            $scope.getData().onSave($scope.model.text);
        }

        $scope.showTextarea();
    };

    $scope.isReadonly = function(){
        if(angular.isFunction($scope.getData().readonly)){
            return $scope.getData().readonly();
        }
        return $scope.getData().readonly;
    }

});


//dialogs
function DialogController($scope, $mdDialog , theScope) {
    $scope.parentScope = theScope;
    $scope.hide = function () {
        $mdDialog.hide();
    };
    $scope.cancel = function () {
        $mdDialog.cancel();
    };
    $scope.answer = function (answer) {
        $mdDialog.hide(answer);
    };
}



