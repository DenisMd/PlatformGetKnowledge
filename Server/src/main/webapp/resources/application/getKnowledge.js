var model = angular.module("mainApp", ["BackEndService", "ui.bootstrap"]);

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

model.controller("inputCtrl",function($scope) {
    $scope.choose = false;
    $scope.model;
    $scope.selectValue;
    $scope.filteredData = [];


    $scope.filter = $scope.getData().filter;
    $scope.id = $scope.getData().id;
    $scope.count = $scope.getData().count;

    $scope.getItem = function (item) {
        if (angular.isObject(item)) {
            return item[$scope.filter];
        } else {
            return item;
        }
    };

    $scope.getFilter = function () {
        //var filter = {};
        //if (!$scope.filter || angular.isArray($scope.getData().list)) {
        //    filter = $scope.model;
        //} else {
        //    filter.list[$scope.filter] = $scope.model;
        //}
        //if ($scope.filteredData) {
        //    if ($scope.filteredData.length == 1) {
        //
        //        if ($scope.model === $scope.filteredData[0]) {
        //            $scope.setModel($scope.filteredData[0]);
        //        }
        //    }
        //}
        //return filter;
    };

    $scope.setSelect = function (value) {
        $scope.choose = value;
    };

    $scope.onEvent = function (event) {
        var elem = angular.element(event.currentTarget);
        switch (event.type) {
            case "mouseover":
                elem.addClass("active");
                break;
            case "mouseout":
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
        if (angular.isObject(value)) {
            $scope.model = value[$scope.filter];
        } else {
            $scope.model = value;
        }
        $scope.selectValue = value;
        $scope.choose = false;
    };

    $scope.open = function () {
        var selector = '#' + $scope.id;
        $(selector).modal('show');
        $scope.resetModel();
    };
});