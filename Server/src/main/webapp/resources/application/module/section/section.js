model.controller("sectionCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    var sectionName = pageService.getPathVariable("section",$state.params.path);
    $scope.test = sectionName;
    applicationService.action($scope, "section" , className.section,"getSectionByNameAndLanguage" , {
        language : $scope.application.language.capitalizeFirstLetter(),
        name :  sectionName
    });

});