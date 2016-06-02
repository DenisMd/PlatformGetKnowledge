model.controller("booksController" , function($scope,applicationService,className){
    var filter = applicationService.createFilter($scope.getData().className,0,10);
    filter.equal("groupBooks.url",$scope.getData().groupBooks);
    filter.equal("groupBooks.section.name",$scope.getData().sectionName);
    $scope.books = [];

    var filter2 = applicationService.createFilter(className.groupBooks,0,1);
    filter2.equal("url" , $scope.getData().groupBooks);
    applicationService.filterRequest($scope,"booksGroup",filter2);

    var addBook = function(book){
        $scope.books.push(book);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"booksData",filter,addBook);
    };

    doAction();

    $scope.loadMore = function () {
        filter.increase(10);
        doAction();
    };

    $scope.folderImg = function(id){
        return applicationService.imageHref($scope.getData().className,id);
    };

    $scope.showAdvanced = function(ev) {
        $scope.showDialog(ev,$scope,"createBook.html",function(answer){
            answer.groupBookId = $scope.booksGroup.list[0].id;
            applicationService.action($scope,"" , className.book,"createBooks",answer,function(result){
                $scope.showToast(result);
                $scope.goTo("book/"+result.object);
            });
        });
    };

    applicationService.list($scope,"langs",className.language, function (item) {
        item.title = $scope.translate(item.name.toLowerCase());
    });
});