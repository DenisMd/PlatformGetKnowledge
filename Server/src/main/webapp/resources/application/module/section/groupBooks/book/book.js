model.controller("bookCtrl", function ($scope,applicationService,className,pageService,$state,$languages) {
    var bookId = pageService.getPathVariable("book",$state.params.path);
    $scope.book = {
        tagsName : []
    };

    function readBook(){
        applicationService.read($scope,"book",className.book,bookId,function(book){
            if (!book) {
                $state.go("404");
            }
            if (!("tags" in book)) {
                book.tagsName = [];
            } else {
                book.tagsName = [];
                book.tags.forEach(function(element){
                    book.tagsName.push(element.tagName);
                });
            }

            book.urls = [];
            book.links.forEach(function (item) {
                book.urls.push({
                    name : item
                });
            });
            
            book.coverUrl = applicationService.imageHref(className.book,book.id);
            book.downloadUrl = applicationService.fileByKeyHref(className.book,book.id,"key");
            book.language = book.language.name.toLowerCase();
            
            if (book.owner){
                book.owner.imageSrc = $scope.userImg(book.owner.id);
                book.owner.userUrl = $scope.createUrl("/user/"+book.owner.id);
            }

            updateCroppedImage(book);
        });
    }

    readBook();

    $scope.addUrl = function() {
        $scope.book.urls.push({
            name : ""
        });
    };

    $scope.removeUrl = function(index) {
        $scope.book.urls.splice(index,1);
    };

    $scope.showEditableContent = false;

    $scope.updateBook = function(book) {
        var result = {};
        result.bookId = book.id;
        result.name = book.name;
        result.authorName = book.authorName;
        result.description = book.description;
        result.language = book.language.capitalizeFirstLetter();
        result.links = [];
        book.urls.forEach(function(element){
            result.links.push(element.name);
        });

        result.tags = book.tagsName;
        applicationService.action($scope,"",className.book,"updateBookInformation",result,function(result){
            $scope.showToast($scope.getResultMessage(result));
            readBook();
        });
    };

    $scope.croppedImg = {
        id : 'book-cover',
        save: function(file){
            updateImage(file);
        },
        areaType:"square"
    };

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"cover",className.book,"uploadCover",{bookId:$scope.book.id},file,function (result) {
            $scope.showToast($scope.getResultMessage(result));
            if (result.status === "Complete") {
                $scope.book.imageViewExist = true;
                $scope.$broadcast("updateCropImage"+$scope.croppedImg.id+"Event");
            }
        });
    };

    function updateCroppedImage(book){
        $scope.croppedImg.src = applicationService.imageHref(className.book,book.id);
        $scope.croppedImg.notUseDefault = book.imageViewExist;

        //Если изображение открывается первый раз событие не сработает так не зарегестрированно
        //Поэтому добавляется проверка для открытия
        $scope.croppedImg.setupImgae = true;

        $scope.$broadcast("updateCropImage"+$scope.croppedImg.id+"Event");
    }

    $scope.langs = $languages.languages;

    $scope.uploadData = {
        btnTitle : "book_data",
        multiplyFiles : false,
        className : className.book,
        actionName : "uploadData",
        title : "book_data",
        parameters : {bookId:+bookId},
        maxFileSize : 50104
    };

    $scope.showDeleteDialog = function(ev) {
        $scope.showConfirmDialog(
            ev,
            $scope.translate("book_delete_dialog_title") + " " + $scope.book.name,
            "",
            'Delete book',
            $scope.translate("delete"),
            $scope.translate("cancel"),
            function () {
                applicationService.remove($scope,"",className.book,$scope.book.id,function (result) {
                    $scope.showToast($scope.getResultMessage(result));
                });
            }
        )
    }

    //Кооментарии к книгам
    $scope.commentData = {
        id : "Book",
        commentClassName : className.bookComment,
        filedName : "book.id",
        objectId : parseInt(bookId),
        withoutEvent : true
    };

});