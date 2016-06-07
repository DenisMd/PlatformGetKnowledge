model.controller("booksController" , function($scope,$state,applicationService,className){

    var maxCharactersInName = 36;

    $scope.currentFilterByDate = true;
    $scope.showCreateArea = false;

    $scope.filter = applicationService.createFilter($scope.getData().className,0,10);
    $scope.filter.createFiltersInfo();
    $scope.filter.equals("groupBooks.url","text",$scope.getData().groupBooks);
    $scope.filter.equals("groupBooks.section.name","text",$scope.getData().sectionName);
    $scope.books = [];

    $scope.by_date = function() {
        $scope.currentFilterByDate = true;
        $scope.filter.clearOrder();
        $scope.filter.clearCustomFilters();
        $scope.filter.setOrder("createDate" , true);

        $scope.filter.result.first = 0;
        $scope.books = [];

        doAction();
    };

    $scope.by_name = function() {
        $scope.currentFilterByDate = false;
        $scope.filter.clearOrder();
        $scope.filter.clearCustomFilters();
        $scope.filter.setOrder("name" , false);

        $scope.filter.result.first = 0;
        $scope.books = [];

        doAction();
    };


    var likeIndex;
    $scope.searchBook = function(text) {
        if (likeIndex != undefined) {
            $scope.filter.result.customFilters.splice(likeIndex,1);
        }
        if (text) {
            likeIndex  = $scope.filter.addCustomFilter("searchBooks",{
                textValue : text
            });
        }

        $scope.filter.result.first = 0;
        $scope.books = [];

        doAction();
    };

    var addBook = function(book){
        book.imageSrc = applicationService.imageHref($scope.getData().className,book.id);
        book.href = $scope.addUrlToPath("/book/" + book.id);
        if (book.name.length > maxCharactersInName) {
            book.name = book.name.substr(0,maxCharactersInName) + "...";
        }
        $scope.books.push(book);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"booksInfo", $scope.filter,addBook);
    };

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
    };

    //Проверка на существование выбранной группы книг
    var groupBookFilter = applicationService.createFilter(className.groupBooks,0,10);
    groupBookFilter.createFiltersInfo();
    groupBookFilter.equals("url","text",$scope.getData().groupBooks);
    applicationService.filterRequest($scope,"groupBookInfo", groupBookFilter,function(groupBook){
        if (groupBook == null) {
            $state.go("404");
        }

        doAction();
    });
});