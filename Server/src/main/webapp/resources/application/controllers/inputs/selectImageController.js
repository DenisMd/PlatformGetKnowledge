//crop image
model.controller("selectImgController", function($scope){
    var getDataSrc = function(){
        return $scope.getData().src+"&" + new Date();
    };

    $scope.originalImg = "";

    var defaultImage = "/resources/image/default/camera.png";

    $scope.uploadFile = function(file,$event) {
        if (file) {
            // ng-img-crop
            var imageReader = new FileReader();
            imageReader.onload = function(image) {
                if ($scope.showModal()) {
                    initModalImage(image,$event);
                } else {
                    $scope.$apply(function($scope) {
                        oldImageSrc = getDataSrc();
                        $scope.originalImg = image.target.result;
                        $scope.onChange($scope.originalImg);
                    });

                }
            };
            imageReader.readAsDataURL(file);
        }
    };


    $scope.getAreaType = function(){
        if ($scope.getData().areaType && angular.isString($scope.getData().areaType)) {
            var type = $scope.getData().areaType.toLowerCase();
            switch (type) {
                case "square":
                case "circle":
                    return type;
            }
        }
        return "circle";
    };

    $scope.onChange = function (element) {
        if (!element) {
            return;
        }

        if (angular.isFunction($scope.getData().save)) {
            var file = base64ToBlob(element);
            $scope.getData().save(file);
        }
    };

    function initModalImage(image,event){
        $scope.$apply(function($scope) {
            $scope.originalImg = image.target.result;
            $scope.item = {
                original : $scope.originalImg,
                cropImage:  ""
            };
            if (!dialogShown) {
                $scope.showDialog(event, $scope, "cropModal.html",$scope.onChange,function(){
                    dialogShown = false;
                    oldImageSrc = getDataSrc();
                    $scope.originalImg = $scope.item.cropImage;
                });
                dialogShown = true;
            }
        });
    }

    $scope.showModal = function(){
        if ($scope.getData()) {
            return $scope.getData().isCrop ? $scope.getData().isCrop : false;
        }
        return false;
    };

    $scope.getClass = function(){
        return $scope.getAreaType() === "circle" ? "img-circle" : "";
    };

    $scope.getImage = function(){
        var notUseDefault = $scope.getData().notUseDefault;
        if (notUseDefault || oldImageSrc) {
            var image = getDataSrc();
            if (image) {
                if (!oldImageSrc) {
                    $scope.originalImg = image;
                } else {
                    if (oldImageSrc !== image){
                        $scope.originalImg =  image;
                        oldImageSrc = "";
                    }
                }
                return $scope.originalImg;
            } else {
                $scope.originalImg = "";
            }
        }
        return defaultImage;
    };

    $scope.getResultSize = function(){
        return $scope.getData().resultSize;
    };

    $scope.getResultQuality = function(){
        return $scope.getData().resultQuality;
    };

    function base64ToBlob(base64Data) {
        var byteString;
        if (base64Data.split(',')[0].indexOf('base64') >= 0) {
            byteString = atob(base64Data.split(',')[1]);
        } else {
            byteString = decodeURI(base64Data.split(',')[1]);
        }
        var mimeString = base64Data.split(',')[0].split(':')[1].split(';')[0];
        var array = [];
        for(var i = 0; i < byteString.length; i++) {
            array.push(byteString.charCodeAt(i));
        }
        var  byteArrays = [new Uint8Array(array)];
        return new Blob(byteArrays, { type: mimeString });
    }
    var oldImageSrc = "";
    var dialogShown = false;
});