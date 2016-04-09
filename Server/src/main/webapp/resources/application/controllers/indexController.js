model.controller("indexController",function($scope){
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
});