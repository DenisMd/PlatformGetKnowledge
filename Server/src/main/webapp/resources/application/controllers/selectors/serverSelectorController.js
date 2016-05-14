model.controller("serverSelectorController" , function ($scope , customFilterService,applicationService) {

    $scope.filter = applicationService.createFilter($scope.getData().className,0,10);

    $scope.list = [];
    $scope.orderItem = "";
    $scope.orderReverse = false;
    $scope.customFilterInfo;
    $scope.showDeleteColumn = false;

    $scope.tableScroll = {
        theme: 'dark-3',
        setHeight: 400,
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: true
        }
    };

    $scope.$on("updateServerSelector" , function (event, args) {
        $scope.filter.reload();
        $scope.list = [];
        doAction();
    });

    var addItem = function(item){
        if (angular.isFunction($scope.getData().callBackForFilter)) {
            $scope.getData().callBackForFilter(item);
        }
        $scope.list.push(item);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"listInfo",$scope.filter,addItem);
    };

    doAction();

    $scope.selectItem = function (item) {
        if (angular.isFunction($scope.getData().selectItemCallback)){
            $scope.getData().selectItemCallback(item);
        }
        var hideColumnForItem = item.hideColumnInfo;
        $scope.list.forEach(function (item) {
            item.hideColumnInfo = true;
        });
        item.hideColumnInfo = !hideColumnForItem;
    };

    $scope.showRowPanel = function(item) {
        return !item.hideColumnInfo && ($scope.getData().actionsForItem || $scope.getData().deleteOptions);
    };

    $scope.setOrder = function(header) {
        if (header.orderBy === true) {
            $scope.orderItem = header.name;
            $scope.orderReverse = !$scope.orderReverse;
            $scope.filter.clearOrder();
            $scope.filter.setOrder(header.name,$scope.orderReverse);
            $scope.filter.reload();
            $scope.list = [];
            doAction();
        }
    };

    $scope.loadMore = function () {
        $scope.filter.increase(10);
        doAction();
    };

    $scope.getItemByName = function(item,name) {
        var splitArr = name.split(".");
        var result = item[splitArr[0]];
        for (var i=1; i < splitArr.length; i++) {
            result = result[splitArr[i]];
        }
        return result;
    };

    $scope.deleteAction = function () {
        $scope.showDeleteColumn = !$scope.showDeleteColumn;
    };

    $scope.openCustomFilter = function(){
        customFilterService.filtersInfo($scope.getData().filters);
        customFilterService.openDialog($scope.customFilterInfo);
        customFilterService.setCallbackSave(function (filter) {
            $scope.customFilterInfo = filter;
            $scope.doFilters();
        });
    };

    $scope.doFilters = function () {
        $scope.filter.deleteFiltersInfo();
        $scope.filter.createFiltersInfo();
        $scope.filter.reload();
        $scope.list = [];

        buildDefaultFilters();
        buildCustomFilters();
        doAction();
    };

    function buildDefaultFilters() {

        for (var i=0; i < $scope.getData().filters.length; i++) {
            var filterData = $scope.getData().filters[i];
            if (filterData.model !== undefined) {
                switch (filterData.type) {
                    case "text" :
                        $scope.filter.like(filterData.field, "text", "%" + filterData.model + "%");
                        break;
                    case "number":
                        $scope.filter.equals(filterData.field, "number", filterData.model);
                        break;
                    case "check_box":
                        $scope.filter.equals(filterData.field, "boolean", filterData.model);
                        break;
                }
            }
        }
    }

    function buildCustomFilters() {
        if (!$scope.customFilterInfo) return;
        $scope.filter.setLogicalExpression($scope.customFilterInfo.logicalOperation);

        for (var i=0; i < $scope.customFilterInfo.filterData.length; i++) {
            var filterItem = $scope.customFilterInfo.filterData[i];
            switch (filterItem.oper.info.name) {
                case "equals" :
                    $scope.filter.equals(filterItem.field.info.name,filterItem.field.info.type,filterItem.param.info.values[0]);
                    break;
                case  "like" :
                    $scope.filter.like(filterItem.field.info.name,"text",filterItem.param.info.values[0]);
                    break;
                case "great_than" :
                    $scope.filter.greatThan(filterItem.field.info.name,"number",filterItem.param.info.values[0]);
                    break;
                case "great_than_or_equal_to" :
                    $scope.filter.greaterThanOrEqualTo(filterItem.field.info.name,"number",filterItem.param.info.values[0]);
                    break;
                case "less_than" :
                    $scope.filter.lessThan(filterItem.field.info.name,"number",filterItem.param.info.values[0]);
                    break;
                case "less_than_or_equal" :
                    $scope.filter.lessThanOrEqualTo(filterItem.field.info.name,"number",filterItem.param.info.values[0]);
                    break;
                case "between" :
                    var type = "number";
                    if (filterItem.field.info.type == "dateTime") {
                        type = "date";
                    }
                    $scope.filter.between(filterItem.field.info.name,type,filterItem.param.info.values[0],filterItem.param.info.values[1]);
                    break;
                case "after" :
                    $scope.filter.greaterThanOrEqualTo(filterItem.field.info.name,"date",filterItem.param.info.values[0]);
                    break;
                case "before" :
                    $scope.filter.lessThanOrEqualTo(filterItem.field.info.name,"date",filterItem.param.info.values[0]);
                    break;
                case "in" :
                    $scope.filter.in(filterItem.field.info.name,filterItem.field.info.type,filterItem.param.info.values);
                    break;
            }
        }
    }
});