model.controller("sectionCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    var sectionName = pageService.getPathVariable("section",$state.params.path);
    $scope.test = sectionName;
    applicationService.action($scope, "section" , className.section,"getSectionByNameAndLanguage" , {
        language : $scope.application.language.capitalizeFirstLetter(),
        name :  sectionName
    } , function(section){
        $scope.sectionCards = {
            title : "categories",
            cardsInRow : 3,
            cards : section.menuItem.subItems,
            prefix : section.menuItem.url
        };
    });

    $scope.sectionImg = function(id){
        return applicationService.imageHref(className.section,id);
    };

});