model.controller("staticSelectorController" , function ($scope , customFilterService) {

    $scope.customFilterInfo;

    $scope.tableScroll = {
        theme: 'dark-3',
        setHeight: 400,
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: true
        }
    };

    $scope.orderItem = "";
    $scope.orderReverse = false;
    $scope.setOrder = function (header) {
        if (header.orderBy === true) {
            $scope.orderItem = header.name;
            $scope.orderReverse = !$scope.orderReverse;
        }
    };


    $scope.filterSearch = function(item,index,allItems)
    {
        //Проверка фильтров по умолчанию
        var isCorrectDefaultFilter = checkDefaultFilters(item);
        if (isCorrectDefaultFilter == false) return isCorrectDefaultFilter;

        if ($scope.customFilterInfo && $scope.customFilterInfo.filterData && $scope.customFilterInfo.filterData.length > 0) {
            return checkCustomFilter(item,allItems);
        } else {
            return isCorrectDefaultFilter;
        }
    };


    function checkDefaultFilters(item) {
        var filtersResult = [];
        for (var i=0; i < $scope.getData().filters.length; i++) {
            var filter = $scope.getData().filters[i];
            switch(filter.type) {
                case "text" :
                    if (filter.model !== undefined) {
                        if (item[filter.field].startsWith(filter.model)) {
                            filtersResult.push(true);
                        } else {
                            filtersResult.push(false);
                        }
                    }
                    break;
                case "number":
                    if (filter.model !== undefined) {
                        if (item[filter.field] === filter.model) {
                            filtersResult.push(true);
                        } else {
                            filtersResult.push(false);
                        }
                    }
                    break;
                case "check_box":
                    if (filter.model !== undefined) {
                        if (item[filter.field] === filter.model) {
                            filtersResult.push(true);
                        } else {
                            filtersResult.push(false);
                        }
                    }
                    break;
            }
        }
        for (i=0; i<filtersResult.length; i++) {
            if (filtersResult[i] === false) {
                return false;
            }
        }

        return true;
    }
    
    function checkCustomFilter(item,allItems) {
        var result = false;
        for (var i=0; i < $scope.customFilterInfo.filterData.length; i++) {
            var filterItem = $scope.customFilterInfo.filterData[i];
            switch (filterItem.oper.info.name) {
                case "equals" :
                    if (item[filterItem.field.info.name] == filterItem.param.info.values[0]) {
                        result = true;
                    } else result = false;
                    break;
                case  "like" : 
                    if (item[filterItem.field.info.name].like(filterItem.param.info.values[0])) {
                        result = true
                    } else result = false;
                    break;
                case "more" :
                    if (item[filterItem.field.info.name] > filterItem.param.info.values[0]) {
                        result = true
                    } else result = false;
                    break;
                case "more_or_equal" :
                    if (item[filterItem.field.info.name] >= filterItem.param.info.values[0]) {
                        result = true
                    } else result = false;
                    break;
                case "less" :
                    if (item[filterItem.field.info.name] < filterItem.param.info.values[0]) {
                        result = true
                    } else result = false;
                    break;
                case "less_or_equal" :
                    if (item[filterItem.field.info.name] <= filterItem.param.info.values[0]) {
                        result = true
                    } else result = false;
                    break;
                case "between" :
                    if (item[filterItem.field.info.name] >= filterItem.param.info.values[0] && item[filterItem.field.info.name] <= filterItem.param.info.values[1]) {
                        result = true
                    } else result = false;
                    break;
                case "after" :
                    if (item[filterItem.field.info.name] > filterItem.param.info.values[0]) {
                        result = true
                    } else result = false;
                    break;
                case "before" :
                    if (item[filterItem.field.info.name] < filterItem.param.info.values[0]) {
                        result = true
                    } else result = false;
                    break;
            }
            if (result) {
                if ($scope.customFilterInfo.logicalOperation == "or") {
                    return true;
                }
            } else {
                if ($scope.customFilterInfo.logicalOperation == "and") {
                    return false;
                }
            }
        }
        return result;
    }

    $scope.openCustomFilter = function(){
        customFilterService.filtersInfo($scope.getData().filters);
        customFilterService.openDialog($scope.customFilterInfo);
    };

    customFilterService.setCallbackSave(function (filter) {
        $scope.customFilterInfo = filter;
    });
});