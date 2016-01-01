model.controller("tasksCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    applicationService.list($scope,"tasks",className.tasks);
    $scope.taskFilter = function(element){
        switch (element.taskStatus){
            case "Complete" : if ($scope.selectTask == 0)return true;break;
            case "Failed" : if ($scope.selectTask == 1)return true;break;
            case "NotStarted" : if ($scope.selectTask == 2)return true;break;
            case "Runnable" : if ($scope.selectTask == 3)return true;break;
        }
        return false;
    }
    $scope.selectTask = "1";

    $scope.currentItem;
    $scope.setCurrentItem = function(item){
        $scope.currentItem = item;
    };




});