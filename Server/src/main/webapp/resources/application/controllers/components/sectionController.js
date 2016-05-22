model.controller("sectionController",function($scope,$state,applicationService,className){

    $scope.sectionCards = {};

    $scope.descriptionScroll = {
        theme: 'dark-3',
        setHeight: 370,
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: true
        }
    };

    applicationService.action($scope, "section" , className.section,"getSectionByName" , {
        name :  $scope.getData().sectionName
    } , function(section){
        //Нету секции
        if (!section){
            $state.go("404");
        }

        section.coverImg = applicationService.imageHref(className.section,section.id);

        $scope.sectionCards = {
            title : "categories",
            cardsInRow : 3,
            cards : section.menuItem.subItems,
            prefix : section.menuItem.url
        };
        $scope.$broadcast("fillCards",$scope.sectionCards);
    });
});