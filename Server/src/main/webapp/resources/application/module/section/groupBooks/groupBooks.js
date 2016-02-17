model.controller("groupBooksCtrl", function ($scope,applicationService,className,pageService,$state) {

    var sectionName = pageService.getPathVariable("section",$state.params.path);

    $scope.folderData = {
        sectionName : sectionName,
        className : className.groupBooks
    };
});