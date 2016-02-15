model.controller("menuCtrl", function ($scope,applicationService,className) {

    $scope.currentMenuItem = null;

    $scope.treeViewListData = {
        dataList : [],
        fieldTitle : "name",
        fieldSubItems : "items",
        subItemFieldTitle : "title",
        subItemFieldSubItems : "subItems",
        callback : function(item) {
            $scope.currentMenuItem = item;
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
          $scope.showToast(result);
      });
    };
});