model.controller("logsCtrl", function ($scope, $state,$http,applicationService,pageService,className) {

    var first = 0;
    var max  = 10;
    var order = "";
    var reverse = false;
    var filter = "";
    var startDate = null;
    var endDate = null;
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

        applicationService.action($scope,"",className.trace,"traceFilter",request,addLog);
    };

    doAction();

    $scope.setLogOrder = function(orderName) {
        reverse = !reverse;
        order = orderName;
        first = 0;
        $scope.logs = [];
        doAction();
    };

    $scope.searchLogs = function(traceLevel) {
        first = 0;
        $scope.logs = [];
        filter = traceLevel;
        doAction();
    };

    $scope.currentLog = null;
    $scope.setCurrentItem = function(item){
        $scope.currentLog = item;
    };

    $scope.loadMore = function () {
        first += 10;
        doAction();
    };

    applicationService.count($scope,"countLogs",className.trace);
});