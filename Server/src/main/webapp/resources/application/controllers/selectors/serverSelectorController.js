model.controller("serverSelectorController" , function ($scope , customFilterService,applicationService) {

    var filter = applicationService.createFilter($scope.getData().className,0,10);

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

    var addItem = function(item){
        $scope.getData().callBackForFilter(item);
        $scope.list.push(item);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"listInfo",filter,addItem);
    };

    doAction();

    $scope.setOrder = function(header) {
        if (header.orderBy === true) {
            $scope.orderItem = header.name;
            $scope.orderReverse = !$scope.orderReverse;
            filter.clearOrder();
            filter.setOrder(header.name,$scope.orderReverse);
            filter.reload();
            $scope.list = [];
            doAction();
        }
    };

    $scope.loadMore = function () {
        filter.increase(10);
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
    };

    customFilterService.setCallbackSave(function (filter) {
        $scope.customFilterInfo = filter;
    });

    $scope.doFilters = function () {
        filter.deleteFiltersInfo();
        filter.createFiltersInfo();
        filter.reload();
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
                        filter.like(filterData.field, "text", "%" + filterData.model + "%");
                        break;
                    case "number":
                        filter.equals(filterData.field, "number", filterData.model);
                        break;
                    case "check_box":
                        filter.equals(filterData.field, "boolean", filterData.model);
                        break;
                }
            }
        }
    }

    function buildCustomFilters() {
        filter.setLogicalExpression($scope.customFilterInfo.logicalOperation);

        for (var i=0; i < $scope.customFilterInfo.filterData.length; i++) {
            var filterItem = $scope.customFilterInfo.filterData[i];
            switch (filterItem.oper.info.name) {
                case "equals" :
                    filter.equals(filterItem.field.info.name,filterItem.field.info.type,filterItem.param.info.values[0]);
                    break;
                case  "like" :
                    filter.like(filterItem.field.info.name,"text",filterItem.param.info.values[0]);
                    break;
                case "great_than" :
                    filter.greatThan(filterItem.field.info.name,"number",filterItem.param.info.values[0]);
                    break;
                case "great_than_or_equal_to" :
                    filter.greaterThanOrEqualTo(filterItem.field.info.name,"number",filterItem.param.info.values[0]);
                    break;
                case "less_than" :
                    filter.lessThan(filterItem.field.info.name,"number",filterItem.param.info.values[0]);
                    break;
                case "less_than_or_equal" :
                    filter.lessThanOrEqualTo(filterItem.field.info.name,"number",filterItem.param.info.values[0]);
                    break;
                case "between" :
                    filter.between(filterItem.field.info.name,"number",filterItem.param.info.values[0],filterItem.param.info.values[1]);
                    break;
                case "after" :
                    filter.greaterThanOrEqualTo(filterItem.field.info.name,"date",filterItem.param.info.values[0]);
                    break;
                case "before" :
                    filter.lessThanOrEqualTo(filterItem.field.info.name,"date",filterItem.param.info.values[0]);
                    break;
                case "in" :
                    filter.in(filterItem.field.info.name,filterItem.field.info.type,filterItem.param.info.values);
                    break;
            }
        }
    }
});