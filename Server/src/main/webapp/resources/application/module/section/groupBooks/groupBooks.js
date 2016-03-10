model.controller("groupBooksCtrl", function ($scope,applicationService,className,pageService,$state) {

    var sectionName = pageService.getPathVariable("section",$state.params.path);

    $scope.groupBook = pageService.getPathVariable("groupBooks",$state.params.path);

    if (!$scope.groupBook) {
        $scope.folderData = {
            sectionName: sectionName,
            className: className.groupBooks
        };
    } else {
        $scope.booksData = {
            sectionName: sectionName,
            groupBooks: $scope.groupBook,
            className: className.book
        }
    }
});