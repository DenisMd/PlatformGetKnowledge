model.controller("menuCtrl", function ($scope,applicationService,className) {

    applicationService.list($scope,"listMenu",className.menu);

    $scope.currentMenuItem = null;
    $scope.setCurrentItem = function(item , level){
        $scope.currentMenuItem = item;
        item.isOpen = !item.isOpen;

        if (level) {
            $scope.currentMenuItem.level = level;
        }
    }
});