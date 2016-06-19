//crop image
model.controller("selectImgController", function($scope){
    var getDataSrc = function(){
        return $scope.getData().src+"&" + new Date();
    };

    var originalImg = "";

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
                        originalImg = image.target.result;
                        $scope.onChange(originalImg);
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
            var file = plUtils.base64ToBlob(element);
            $scope.getData().save(file);
        }
    };

    function initModalImage(image,event){
        $scope.$apply(function($scope) {
            originalImg = image.target.result;
            $scope.item = {
                original : originalImg,
                cropImage:  ""
            };
            if (!dialogShown) {
                $scope.showDialog(event, $scope, "cropModal.html",$scope.onChange,function(){
                    dialogShown = false;
                    oldImageSrc = getDataSrc();
                    originalImg = $scope.item.cropImage;
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
    
    $scope.imageUrl = defaultImage;

    function getImage(){
        var notUseDefault = $scope.getData().notUseDefault;
        if (notUseDefault || oldImageSrc) {
            var image = getDataSrc();
            if (image) {
                if (!oldImageSrc) {
                    originalImg = image;
                } else {
                    if (oldImageSrc !== image){
                        originalImg =  image;
                        oldImageSrc = "";
                    }
                }
                return originalImg;
            } else {
                originalImg = "";
            }
        }
        return defaultImage;
    }

    //При иницализации если задано изображение сразу установить его
    if ($scope.getData().setupImgae) {
        $scope.imageUrl = getImage();
    }

    $scope.$on("updateCropImage"+$scope.getData().id+"Event",function (event,args) {
        $scope.imageUrl = getImage();
    });

    $scope.getResultSize = function(){
        return $scope.getData().resultSize ? $scope.getData().resultSize : 400;
    };

    $scope.getResultQuality = function(){
        return $scope.getData().resultQuality ? $scope.getData().resultQuality : 1.0;
    };
    var oldImageSrc = "";
    var dialogShown = false;
});