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

    $scope.getVideoUrl = function (id) {
        return "/data/readVideo?className=com.getknowledge.modules.video.Video&id="+id;
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

    applicationService.pageInfo($scope);
    applicationService.action($scope, "menu", "com.getknowledge.modules.menu.Menu", "getMenu", {});
    applicationService.action($scope, "user", "com.getknowledge.modules.userInfo.UserInfo", "getAuthorizedUser", {});
});

model.controller("cardCtrl", function ($scope,$window) {
    $scope.cards = [];
    update();

    $scope.range = function() {
        update();
        return new Array($scope.count);
    };

    var cardsArray = [];
    $scope.maxLength = 3;
    $scope.prepareCard = function(index) {
        if (!cardsArray[index]) {
            cardsArray[index] = [];
        }
        var realIndex = index * $scope.maxLength;
        if ($scope.cards.length > realIndex){
                updateArray(index,$scope.cards.slice(realIndex, $scope.maxLength+realIndex));
        } else {
            updateArray(index,$scope.cards.slice(realIndex));
        }
        return cardsArray[index];
    };

    function updateArray(index,newArray){
        if (!angular.equals(cardsArray[index], newArray)){
            cardsArray[index] = newArray;
        }
    }

    function update(){
        if ($scope.menu) {
            angular.copy($scope.menu.items, $scope.cards);
            if ($scope.cards) {
                var len = $scope.cards.length;
                $scope.count = $scope.maxLength? Math.ceil(len / $scope.maxLength) : 0;
            } else {
                $scope.count = 0;
            }
        }
    }
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
