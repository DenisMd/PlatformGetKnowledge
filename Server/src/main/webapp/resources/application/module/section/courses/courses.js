model.controller("coursesCtrl", function ($scope,pageService,$state) {
    var sectionName = pageService.getPathVariable("section",$state.params.path);
    $scope.test = sectionName;
});