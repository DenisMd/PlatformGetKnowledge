model.controller("tasksCtrl", function ($scope, $state,$http,applicationService,pageService,className) {

    var filter = applicationService.createFilter(className.tasks,0,10);
    $scope.tasks = [];

    var addTask = function(task){
        $scope.tasks.push(task);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"",filter,addTask);
    };

    doAction();

    $scope.setCurrentItem = function(item){
        $scope.currentTask = item;
    };

    $scope.searchTasks = function(taskStatus) {
        filter.first = 0;
        $scope.tasks = [];
        filter.equal("taskStatus",taskStatus);
        doAction();
    };

    var reverse = false;
    $scope.setTaskOrder = function(orderName) {
        reverse = !reverse;
        filter.clearOrder();
        filter.setOrder(orderName,reverse);
        filter.first = 0;
        $scope.tasks = [];
        doAction();
    };

    applicationService.count($scope,"countTasks",className.tasks);

});