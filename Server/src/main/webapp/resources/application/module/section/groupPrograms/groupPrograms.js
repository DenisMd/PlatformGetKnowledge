model.controller("groupProgramsCtrl", function ($scope,applicationService,className,pageService,$state) {

    var sectionName = pageService.getPathVariable("section",$state.params.path);
    $scope.groupProgram = pageService.getPathVariable("groupPrograms",$state.params.path);

    if (!$scope.groupProgram) {
        $scope.folderData = {
            sectionName: sectionName,
            className: className.groupPrograms,
            title : $scope.translate("programs_title"),
            totalCountFieldName : "programsCount"
        };
    } else {
        $scope.programData = {
            sectionName: sectionName,
            groupProgram: $scope.groupProgram,
            className: className.program
        };
    }
});