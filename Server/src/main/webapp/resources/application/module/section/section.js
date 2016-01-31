model.controller("sectionCtrl", function ($scope,pageService,$state) {
    var sectionName = pageService.getPathVariable("section",$state.params.path);
    $scope.sectionData = {
        sectionName : sectionName
    };
});