var model = angular.module("mainApp", ["BackEndService", "ui.bootstrap", "ngImgCrop"]);

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
};

model.controller("mainController", function ($scope,$rootScope, $http, $state, applicationService, className) {

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
            applicationService.action($scope, "user", className.userInfo, "getAuthorizedUser", {});
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

    applicationService.action($scope, "menu", className.menu, "getMenu", {}, function(menu){
        $scope.cardsData = {
            cardsInRow : 3,
            cards : menu.items
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

model.controller("inputCtrl",function($scope,$sce,$filter,$document) {
    $scope.choose = false;
    $scope.model;
    $scope.modalModel;
    $scope.selectModalValue;
    $scope.selectValue;

    $scope.filter = $scope.getData().filter;
    $scope.id = $scope.getData().id;
    $scope.count = $scope.getData().count;
    $scope.class = $scope.getData().class;
    $scope.callback = angular.isFunction($scope.getData().callback)? $scope.getData().callback : null;
    $scope.required = $scope.getData().required;
    $scope.list = [];
    var selector = '#' + $scope.id;

    $scope.closeModal = function(){
        $(selector).modal("hide");
        $scope.resetActiveElementInModal();
        $scope.modalModel = "";
        $scope.selectModalValue = null;
    } ;
    $scope.getItem = function (item) {
        if (item.$$unwrapTrustedValue) {
            return item;
        } else {
            return item[$scope.filter];
        }
    };

    $scope.getList = function(){
        if (!$scope.list.length){
            $scope.list = $scope[$scope.getData().listName]? $scope[$scope.getData().listName]:[];
        }
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
                $scope.selectForm['main-select'].$setValidity("selectValue", true);
                if (filteredData.length === 1) {
                    if ($scope.model.toString() === $scope.getItem(filteredData[0]).toString()) {
                        $scope.setModel(filteredData[0]);
                    }
                } else {
                    if (filteredData.length === 0) {
                        $scope.selectForm['main-select'].$setValidity("selectValue", false);
                    }
                }
            }
            if (!$scope.choose && filteredData.length !== list.length){
                $scope.selectForm['main-select'].$setValidity("selectValue", false);
            }
        } else {
            if (filteredData.length === 0){
                $scope.selectForm['search-input'].$setValidity("searchValue", false);
            } else {
                $scope.selectForm['search-input'].$setValidity("searchValue", true);
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
        $scope.model = null;
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
        $scope.selectValue = $scope.selectModalValue;
        $scope.model = getValue($scope.selectModalValue);
        $('#' + $scope.id).modal('hide');
        $scope.resetActiveElementInModal();
    };

    $scope.hideSelect = function(){
        $scope.$apply(function () {
            $scope.choose = false;
        });
    };

    $scope.selectScrollConfig = {
        theme: 'dark-3',
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: true
        },
        setHeight : getHeight()
    };

    function getHeight(){
        var temp = 40 * $scope.getList().length;
        return !temp || temp > 400? 400 : temp;
    }

    function getValue(value){
        if (angular.isString(value) || value.$$unwrapTrustedValue) {
            return value;
        } else {
            return value[$scope.filter];
        }
    }
});

model.controller("selectImgCtrl", function($scope){
    $scope.originalImg='';
    $scope.croppedImg='';
    var handleFileSelect=function(evt) {
        var file=evt.currentTarget.files[0];
        var reader = new FileReader();
        reader.onload = function (evt) {
            $scope.$apply(function($scope){
                $scope.originalImg=evt.target.result;
                $('#myModal').modal('show');
            });
        };
        reader.readAsDataURL(file);
    };
    angular.element('#fileInput').on('change',handleFileSelect);
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
