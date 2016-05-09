model.controller("treeListController" , function ($scope) {
    $scope.setCurrentItem = function(item , level){
        item.isOpen = !item.isOpen;
        $scope.getData().callback(item , level);
    };
});