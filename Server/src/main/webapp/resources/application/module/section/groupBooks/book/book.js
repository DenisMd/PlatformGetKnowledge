model.controller("bookCtrl", function ($scope,applicationService,className,pageService,$state,FileUploader) {
    var bookId = pageService.getPathVariable("book",$state.params.path);
    $scope.book = {
        tagsName : []
    };

    function readBook(){
        applicationService.read($scope,"book",className.book,bookId,function(book){
            if (!("tags" in book)) {
                book.tagsName = [];
            } else {
                book.tagsName = [];
                book.tags.forEach(function(element){
                    book.tagsName.push(element.tagName);
                })
            }

            $scope.book.urls = [];
            book.links.forEach(function (item) {
                $scope.book.urls.push({
                    name : item
                });
            });
        });
    };

    readBook();
    $scope.bookImg = function(){
        return applicationService.imageHref(className.book,bookId);
    };

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
        result.description = book.description;
        result.links = [];
        book.urls.forEach(function(element){
            result.links.push(element.name);
        });

        result.tags = book.tagsName;
        applicationService.action($scope,"",className.book,"updateBookInformation",result,function(result){
            $scope.showToast(result);
            readBook();
        });
    }

    var croppedImg = {
        save: function(file){
            updateImage(file);
        },
        areaType:"square"
    };

    $scope.getCropImageData  = function(){
        croppedImg.src = applicationService.imageHref(className.book,$scope.book.id);
        croppedImg.notUseDefault = $scope.book.imageViewExist;
        return croppedImg;
    };

    var updateImage = function(file) {
        applicationService.actionWithFile($scope,"",className.book,"uploadCover",{bookId:$scope.book.id},file);
    };

    $scope.uploader = applicationService.createUploader($scope,"",className.book,"uploadData",{bookId:+bookId});

    $scope.bookData = function(){
        return applicationService.fileByKeyHref(className.book,bookId,"key");
    };


});