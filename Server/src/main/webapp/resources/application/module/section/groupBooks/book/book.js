model.controller("bookCtrl", function ($scope,applicationService,className,pageService,$state) {
    var bookId = pageService.getPathVariable("book",$state.params.path);
    applicationService.read($scope,"book",className.books,bookId);
    $scope.bookImg = function(){
        return applicationService.imageHref(className.books,bookId);
    };
});