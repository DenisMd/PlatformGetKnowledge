model.controller("booksController" , function($scope,$state,$languages,applicationService,className){

    var maxCharactersInName = 21;

    $scope.currentFilterByDate = true;
    $scope.showCreateArea = false;

    $scope.filter = applicationService.createFilter($scope.getData().className,0,12);
    $scope.filter.createFiltersInfo();
    $scope.filter.equals("groupBooks.url","text",$scope.getData().groupBooks);
    $scope.filter.equals("groupBooks.section.name","text",$scope.getData().sectionName);
    $scope.books = [];

    $scope.by_date = function() {
        $scope.currentFilterByDate = true;
        $scope.filter.clearOrder();
        $scope.filter.setOrder("createDate" , true);

        $scope.filter.result.first = 0;
        $scope.books = [];

        doAction();
    };

    $scope.by_name = function() {
        $scope.currentFilterByDate = false;
        $scope.filter.clearOrder();
        $scope.filter.setOrder("name" , false);

        $scope.filter.result.first = 0;
        $scope.books = [];

        doAction();
    };

    var likeIndex;
    var equalIndex;
    $scope.searchBook = function(text,language) {
        if (likeIndex !== undefined) {
            $scope.filter.result.customFilters.splice(likeIndex,1);
        }

        if (text) {
            likeIndex  = $scope.filter.addCustomFilter("searchBooks",{
                textValue : text
            });
        } else {
            likeIndex = undefined;
        }

        if (equalIndex !== undefined) {
            $scope.filter.result.filtersInfo.filters.splice(equalIndex, 1);
        }

        if (language && language != "any") {
            equalIndex = $scope.filter.equals("language.name", "str",language.capitalizeFirstLetter());
        } else {
            equalIndex = undefined;
        }

        //$scope.filter.like("tags.tagName","text","%"+text+"%");

        $scope.filter.result.first = 0;
        $scope.books = [];

        doAction();
    };

    var addBook = function(book){
        if(book) {
            book.imageSrc = applicationService.imageHref($scope.getData().className, book.id);
            book.href = $scope.addUrlToPath("/book/" + book.id);
            if (book.name.length > maxCharactersInName) {
                book.name = book.name.substr(0, maxCharactersInName) + "...";
            }
            $scope.books.push(book);
        }
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"booksInfo", $scope.filter,addBook);
    };

    $scope.loadMore = function () {
        $scope.filter.increase(12);
        doAction();
    };

    $scope.langs = $languages.languages;

    $scope.createBook = function(newBook) {
        newBook.groupBookUrl = $scope.getData().groupBooks;
        newBook.language = newBook.language.capitalizeFirstLetter();
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
    groupBookFilter.equals("section.name","text",$scope.getData().sectionName);
    applicationService.filterRequest($scope,"groupBookInfo", groupBookFilter,function(groupBook){
        if (groupBook == null) {
            $state.go("404");
        }

        $scope.by_date();
    });
});