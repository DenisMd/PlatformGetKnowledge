model.controller("logsCtrl", function ($scope, $state,$http,applicationService,pageService,className) {


    var filter = applicationService.createFilter(className.trace,0,10);
    $scope.logs = [];

    var addLog = function(log){
        switch (log.traceLevel){
            case "Error": log.iconClassName = "fa-exclamation-circle red-error"; break;
            case "Critical": log.iconClassName = "fa-exclamation-triangle red-critical"; break;
            case "Warning": log.iconClassName = "fa-exclamation color-warning"; break;
            case "Event": log.iconClassName = "fa-eye color-event"; break;
            case "Debug": log.iconClassName = "fa-circle-thin color-debug"; break;
        }
        $scope.logs.push(log);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"",filter,addLog);
    };

    doAction();

    var reverse = false;
    $scope.setLogOrder = function(orderName) {
        reverse = !reverse;
        filter.clearOrder();
        filter.setOrder(orderName,reverse);


        filter.reload();
        $scope.logs = [];
        doAction();
    };

    $scope.searchLogs = function(traceLevel) {
        if (!traceLevel) {
            filter.clearIn();
        } else {
            filter.in("traceLevel" , [traceLevel]);
        }
        filter.reload();
        $scope.logs = [];
        doAction();
    };

    $scope.currentLog = null;
    $scope.setCurrentItem = function(item){
        $scope.currentLog = item;
    };

    $scope.loadMore = function () {
        filter.increase(10);
        doAction();
    };

    applicationService.count($scope,"countLogs",className.trace);
});