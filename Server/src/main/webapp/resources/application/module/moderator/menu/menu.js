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

    $scope.updateMenu = function () {
      var  tempClassName = className.menu;
      if ("url" in $scope.currentMenuItem) {
          tempClassName = className.menuItem;
      }
      applicationService.update($scope,"",tempClassName,$scope.currentMenuItem,function(result){
          $scope.showToast(result);
      });
    };
});