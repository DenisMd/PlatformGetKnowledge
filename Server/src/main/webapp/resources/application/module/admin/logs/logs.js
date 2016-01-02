model.controller("logsCtrl", function ($scope, $state,$http,applicationService,pageService,className) {
    applicationService.list($scope,"logs",className.trace,function(item, index, array){
        switch (item.traceLevel){
            case "Error": item.iconClassName = "fa-exclamation-circle red-error"; break;
            case "Critical": item.iconClassName = "fa-exclamation-triangle red-critical"; break;
            case "Warning": item.iconClassName = "fa-exclamation color-warning"; break;
            case "Event": item.iconClassName = "fa-eye color-event"; break;
            case "Debug": item.iconClassName = "fa-circle-thin color-debug"; break;
        }
    });

    $scope.currentItem;
    $scope.setCurrentItem = function(item){
        $scope.currentItem = item;
    };
    var reverse = false;
    $scope.setOrder = function (order) {
        reverse = !reverse;
        $scope.order = reverse?"-"+order:order;
    };
    $scope.eventFilter = function(element){
        switch (element.traceLevel){
            case "Debug" : if ($scope.selectEvent == 0)return true;break;
            case "Event" : if ($scope.selectEvent <= 1)return true;break;
            case "Warning" : if ($scope.selectEvent <= 2)return true;break;
            case "Error" : if ($scope.selectEvent <= 3)return true;break;
            case "Critical" : if ($scope.selectEvent <= 4)return true;break;
        }
        return false;
    };
    $scope.selectEvent = "1";
});