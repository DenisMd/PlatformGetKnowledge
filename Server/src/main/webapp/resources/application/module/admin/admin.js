model.controller("adminCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    applicationService.action($scope, "section" , className.section,"getSectionByNameAndLanguage" , {
        language : $scope.application.language.capitalizeFirstLetter(),
        name :  "admin"
    } , function(section){
        $scope.sectionCards = {
            title : "categories",
            cardsInRow : 3,
            cards : section.menuItem.subItems,
            prefix : section.menuItem.url
        };
    });

});