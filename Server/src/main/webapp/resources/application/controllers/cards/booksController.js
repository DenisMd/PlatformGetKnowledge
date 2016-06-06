model.controller("booksController" , function($scope,applicationService,className){

    $scope.currentFilterByDate = true;
    
    $scope.showCreateArea = false;

    $scope.filter = applicationService.createFilter($scope.getData().className,0,10);
    $scope.filter.createFiltersInfo();
    $scope.filter.equals("groupBooks.url","text",$scope.getData().groupBooks);
    $scope.filter.equals("groupBooks.section.name","text",$scope.getData().sectionName);
    $scope.books = [];

    var addBook = function(book){
        book.imageSrc = applicationService.imageHref($scope.getData().className,book.id);
        $scope.books.push(book);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"booksInfo", $scope.filter,addBook);
    };

    doAction();

    $scope.loadMore = function () {
        $scope.filter.increase(10);
        doAction();
    };

    applicationService.list($scope,"langs",className.language, function (item) {
        item.title = $scope.translate(item.name.toLowerCase());
    });

    $scope.createBook = function(newBook) {
        newBook.groupBookUrl = $scope.getData().groupBooks;
        applicationService.action($scope,"",className.book,"createBook",newBook,function(result){
            $scope.showToast($scope.getResultMessage(result));
            if (result.status == "Complete") {
                $scope.goTo("book/" + result.object);
            }
        });
    }
});