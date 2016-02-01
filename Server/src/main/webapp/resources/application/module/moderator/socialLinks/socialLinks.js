model.controller("socialLinksCtrl", function ($scope,applicationService,className) {

    $scope.setCurrentItem = function (item) {
        $scope.currentLink = item;
    };

    $scope.updateRole = function(){
        applicationService.update($scope,"links",className.socialLinks,$scope.currentLink,function(result){
            $scope.showToast(result);
        });
    };

    applicationService.list($scope, "links", className.socialLinks);
});