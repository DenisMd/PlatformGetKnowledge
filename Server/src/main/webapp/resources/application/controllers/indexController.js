model.controller("indexController",function($scope,applicationService,className){
    //--------------------------------------------- опции слайдера
    $scope.carouselData = {
        interval : 5000,
        slides : [
            {
                section: "Programming",
                image: "/resources/image/index/carousel/programming.jpg",
                text: "carousel_programming"
            },
            {
                section: "Math",
                image: "/resources/image/index/carousel/math.jpg",
                text: "carousel_math"
            },
            {
                section: "Physic",
                image: "/resources/image/index/carousel/physic.jpg",
                text: "carousel_physic"
            },
            {
                section: "Design",
                image: '/resources/image/index/carousel/design.jpg',
                text: "carousel_design"
            }
        ]
    };

    //Первое видео
    $scope.indexVideo1 = {
        id : 1,
        showComments : true
    };

    //Первое видео
    $scope.indexVideo2 = {
        id : 2,
        showComments : true
    };

    //информация о пунктах меню через "карточки"
    applicationService.action($scope, "", className.menu, "getMenu", {}, function(menu){
        $scope.cardsData = {
            title : "our_courses",
            cardsInRow : 3,
            cards : menu.items,
            prefix : ''
        };
        $scope.$broadcast("fillCards",$scope.cardsData);
    });

    //создаем массив по диапозону
    $scope.getRow = function (index, length, array) {
        var result = [];
        for (var i = index*length; i < length*(index+1); i++) {
            if (array.length <= i) {
                return result;
            }
            result.push(array[i]);
        }
        return result;
    };

});