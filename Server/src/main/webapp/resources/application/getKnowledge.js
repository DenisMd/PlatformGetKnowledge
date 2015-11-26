var model = angular.module("mainApp", ["BackEndService", "ui.bootstrap", "ngImgCrop"]);

model.controller("mainController", function ($scope,$rootScope, $http, $state, applicationService, className) {
    $scope.menuScrollConfig = {
        theme: 'light-3',
        snapOffset: 100,
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: "ul li"
        }
    };

    $scope.getVideoUrl = function (id) {
        return "/data/readVideo?className="+className.video+"&id="+id;
    };

    $scope.logout = function(){
        if (!$scope.user) return;
        $http.get("/j_spring_security_logout").success(function(){
            applicationService.action($scope, "user", className.userInfo, "getAuthorizedUser", {});
        });
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

    $scope.toggelMenu = true;

    $scope.toggelClick = function () {
        $scope.toggelMenu = !$scope.toggelMenu;
        var wrapper = angular.element("#wrapper");
        wrapper.toggleClass("wrapper-left");
    };

    $scope.translate = function (key) {
        if (!$scope.application || !$scope.application.text || !(key in $scope.application.text)) {
            return key;
        }

        return $scope.application.text[key];

    };

    $scope.createUrl = function (url) {
        if (!$scope.application) return;
        return '#/' + $scope.application.language + url;
    };

    $scope.range = function(n) {
        if (!n) return 1;
        return new Array(Math.ceil(n));
    };

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

    $scope.getRow = function (index, length, array) {
        var result = [];
        for (var i = index*length; i < length*(index+1); i++) {
            if (array.length <= i) return result;
            result.push(array[i]);
        }
        return result;
    };

    $scope.userImg = function(id){
        return applicationService.imageHref(className.userInfo,id);
    };

});

model.controller("videoCtrl",function($scope){
    init();

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

    var player;

    function init(){
        if (!player){
            var options = {
                "controls": true,
                "preload" : "matadata",
                "autoplay" : false,
                "width": 720,
                "height":480
            };
            player = videojs('main-video', options, function () {
                player = this;
            });
        }
    }
});

model.controller("inputCtrl",function($scope,$sce,$filter,$document) {
    $scope.choose = false;
    $scope.model;
    $scope.selectValue;
    var filteredData = [];
    var chooseElement = angular.element(document.getElementsByClassName("select-with-input"))[0];



    $scope.filter = $scope.getData().filter;
    $scope.id = $scope.getData().id;
    $scope.count = $scope.getData().count;
    $scope.class = $scope.getData().class;

    $scope.getItem = function (item) {
        if (item.$$unwrapTrustedValue) {
            return item;
        } else {
            return item[$scope.filter];
        }
    };

    $scope.getList = function(){
        return $scope.getData().list;
    };

    $scope.getFilteredData = function () {

        var filter = {};
        if (!$scope.filter || angular.isArray($scope.getList())) {
            filter = $scope.model;
        } else {
            filter.list[$scope.filter] = $scope.model;
        }
        filteredData = $filter('filter')($scope.getList(),filter);
        filteredData = $filter('limitTo')(filteredData, $scope.count);
        if (filteredData) {
            $scope.selectForm['main-select'].$setValidity("selectValue", true);
            if (filteredData.length === 1) {
                if ($scope.model.toString() === filteredData[0].toString()) {
                    $scope.setModel(filteredData[0]);
                }
            } else {
                if (filteredData.length === 0){
                    $scope.selectForm['main-select'].$setValidity("selectValue", false);
                }
            }
        }
        if (filteredData.length !== 1 || $scope.model.toString() !== filteredData[0].toString()){
            $scope.selectForm['main-select'].$setValidity("selectValue", false);
        }
        return filteredData;
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
        $scope.filteredData = [];
        $scope.choose = false;
    };

    $scope.setModel = function (value) {
        if (value.$$unwrapTrustedValue) {
            $scope.model = value;
        } else {
            $scope.model = value[$scope.filter];
        }
        $scope.selectValue = value;
        $scope.choose = false;
    };

    $scope.open = function () {
        var selector = '#' + $scope.id;
        $(selector).modal('show');
        $scope.resetModel();
    };

    $scope.hideSelect = function(){
        $scope.$apply(function () {
            $scope.choose = false;
        });
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
    angular.element(document.querySelector('#fileInput')).on('change',handleFileSelect);
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
