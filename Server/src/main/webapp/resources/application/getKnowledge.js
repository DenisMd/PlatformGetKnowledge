var model = angular.module("mainApp", ["BackEndService", "ngAnimate", "ui.bootstrap"]);
model.controller("mainController", function ($scope, $http, $state, applicationService) {
    $scope.menuScrollConfig = {
        theme: 'light-3',
        snapOffset: 100,
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: "ul li"
        }
    };

    //смена языка
    $scope.changeLanguage = function (language) {
        if (!$scope.application.language || $scope.application.language === language) {
            return;
        }
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
        applicationService.pageInfo($scope);
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

    applicationService.pageInfo($scope);
    applicationService.action($scope, "menu", "com.getknowledge.modules.menu.Menu", "getMenu", {});
    applicationService.action($scope, "user", "com.getknowledge.modules.userInfo.UserInfo", "getAuthorizedUser", {});
});

model.controller("carouselCtrl", function ($scope) {
    $scope.interval = 5000;
    $scope.noWrapSlides = false;
    $scope.slides = [
        {
            section: "Programming",
            image: "/resources/image/index/slider/programming.jpg",
            text: "Программирование - способ научиться формулировать свои желания компьютеру."
        },
        {
            section: "Math",
            image: "/resources/image/index/slider/math.jpg",
            text: "Математика - одна истина прекрасна."
        },
        {
            section: "Physic",
            image: "/resources/image/index/slider/physic.jpg",
            text: "Если тебя квантовая физика не испугала, значит, ты ничего в ней не понял."
        },
        {
            section: "Design",
            image: '/resources/image/index/slider/design.jpg',
            text: "Простота — необходимое условие прекрасного."
        }
    ];
});

model.controller("cardCtrl", function ($scope,$window) {
    $scope.cards = [];
    $scope.style = {};
    $scope.getCards = function(){
        if ($scope.menu) {
            updateArray($scope.menu.items, $scope.cards);
        }
        return $scope.cards;
    };


    function updateArray(newArray,oldArray){
        if (!angular.equals(oldArray, newArray)){
            angular.copy(newArray, oldArray);
        }
        return oldArray;
    }
    $scope.style = {};
    //$scope.update = function (){
    //    var wrapper = angular.element("#wrapper");
    //    var cardsArray = $(".thumbnail-card").get();
    //    var objectsHeight = [];
    //    if (cardsArray.length){
    //        cardsArray.forEach(function(item,index){
    //            var object = $(item);
    //            objectsHeight.push(object.innerHeight());
    //        });
    //    }
    //    var height =  0;
    //    if (objectsHeight.every(function(element) {
    //            return element === objectsHeight[0];
    //        })){
    //        height =  objectsHeight[0];
    //    } else{
    //        height =  Math.max.apply(null, objectsHeight);
    //        height += 35; //fotter
    //    }
    //
    //    var style = {
    //        "min-height" : height
    //    };
    //    if (!angular.equals($scope.style,style)){
    //        $scope.style = style;
    //    }
    //};

    //angular.element($window).bind('resize', function() {
    //    $scope.update();
    //    $scope.$apply();
    //});
});

model.controller("videoCtrl",function($scope){
    $scope.videoSetup = {
        "controls": true,
        "preload" : "auto",
        "autoplay" : false,
        "width": 720,
        "height":480
    };
    $scope.open = function() {
        if (!player) {
            player = videojs('main-video', $scope.videoSetup, function () {

                player = this;

            });
            player.src({type: "video/mp4", src: "/data/readVideoFile"});
        }
        $('#videoModal').modal('show');
    };

    $scope.close = function(){
        $('#videoModal').modal('hide');

    };

    var player;
});

//model.directive('test', function() {
//    return {
//        restrict: 'A',
//        link: function(scope, element, attrs) {
//                if (scope.$last) {
//                    scope.$eval('update()');
//                }
//            }
//
//    }
//});

