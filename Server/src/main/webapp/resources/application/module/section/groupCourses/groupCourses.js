model.controller("groupCoursesCtrl", function ($scope,applicationService,className,pageService,$state) {

    var sectionName = pageService.getPathVariable("section",$state.params.path);
    $scope.groupCoursesName = pageService.getPathVariable("groupCourses" , $state.params.path);

    $scope.folderData = {
        sectionName : sectionName,
        className : className.groupCourses
    };
});