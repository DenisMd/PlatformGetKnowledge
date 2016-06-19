model.controller("uploadFilesController", function($scope,applicationService){

    $scope.uploader = applicationService.createUploader($scope,"",
                                                        $scope.getData().className,
                                                        $scope.getData().actionName,
                                                        $scope.getData().parameters,
                                                        $scope.getData().callback,
                                                        $scope.getData().prepareParams);
    $scope.onChange = function (element) {
        if (!element) {
            return;
        }

        if (angular.isFunction($scope.getData().save)) {
            var file = plUtils.base64ToBlob(element);
            $scope.getData().save(file);
        }
    };

    $scope.showFileDialog = function ($event) {
        $scope.showDialog($event, $scope, "uploadFileDialog.html",$scope.onChange);
    };
    
});