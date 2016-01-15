model.controller("tasksCtrl", function ($scope, $state,$http,applicationService,pageService,className) {

    var first = 0;
    var max  = 10;
    var order = "";
    var reverse = false;
    var filter = "";
    var startDate = null;
    var endDate = null;
    $scope.tasks = [];

    var addTask = function(task){
        $scope.tasks.push(task);
    };

    var doAction = function(){
        var request = {
            "first" : first,
            "max" : max,
            "order" : order
        };

        if (reverse){
            request.desc = "desc";
        }

        if (filter){
            request.filter = filter;
        }

        if (startDate != null && endDate != null) {
            request.startDate = startDate;
            request.endDate = endDate;
        }

        applicationService.action($scope,"",className.tasks,"taskFilter",request,addTask);
    };

    doAction();

    $scope.setCurrentItem = function(item){
        $scope.currentTask = item;
    };

    $scope.searchTasks = function(taskStatus) {
        console.log("dasdasda " + taskStatus);
        first = 0;
        $scope.tasks = [];
        filter = taskStatus;
        doAction();
    };

    $scope.setTaskOrder = function(orderName) {
        reverse = !reverse;
        order = orderName;
        first = 0;
        $scope.tasks = [];
        doAction();
    };

    applicationService.count($scope,"countTasks",className.tasks);

});