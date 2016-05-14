model.controller("menuCtrl", function ($scope,applicationService,className) {

    $scope.currentMenuItem = null;

    $scope.treeViewListData = {
        dataList : [],
        topField : "name",
        topSubItems : "items",
        subItemFieldTitle : "title",
        subItemFieldSubItems : "subItems",
        callback : function(item,level) {
            $scope.currentMenuItem = item;
            $scope.currentMenuItem.level = level;
        }
    };

    applicationService.list($scope,"listMenu",className.menu, function (item) {
        $scope.treeViewListData.dataList.push(item);
    });

    $scope.updateMenu = function () {
      var  tempClassName = className.menu;
      if ("url" in $scope.currentMenuItem) {
          tempClassName = className.menuItem;
      }
      applicationService.update($scope,"",tempClassName,$scope.currentMenuItem,function(result){
          $scope.showToast($scope.getResultMessage(result));
      });
    };
});