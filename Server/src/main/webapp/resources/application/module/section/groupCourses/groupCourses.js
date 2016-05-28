model.controller("groupCoursesCtrl", function ($scope,applicationService,className,pageService,$state) {

    var sectionName = pageService.getPathVariable("section",$state.params.path);
    $scope.groupCourses = pageService.getPathVariable("groupCourses" , $state.params.path);

    if (!$scope.groupCourses) {
        $scope.folderData = {
            sectionName: sectionName,
            className: className.groupCourses,
            title: $scope.translate("courses_title")
        };
    } else {
        $scope.coursesData = {
            sectionName : sectionName,
            groupName : $scope.groupCourses
        };
    }


});