model.controller("clientSelectorController" , function ($scope , customFilterService) {

    $scope.customFilterInfo;

    $scope.showDeleteColumn = false;

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
                    result = item[filterItem.field.info.name] == filterItem.param.info.values[0];
                    break;
                case  "like" : 
                    result = item[filterItem.field.info.name].like(filterItem.param.info.values[0]);
                    break;
                case "great_than" :
                    result = item[filterItem.field.info.name] > filterItem.param.info.values[0];
                    break;
                case "great_than_or_equal_to" :
                    result = item[filterItem.field.info.name] >= filterItem.param.info.values[0];
                    break;
                case "less_than" :
                    result = item[filterItem.field.info.name] < filterItem.param.info.values[0];
                    break;
                case "less_than_or_equal" :
                    result = item[filterItem.field.info.name] <= filterItem.param.info.values[0];
                    break;
                case "between" :
                    result = item[filterItem.field.info.name] >= filterItem.param.info.values[0] && item[filterItem.field.info.name] <= filterItem.param.info.values[1];
                    break;
                case "after" :
                    result = item[filterItem.field.info.name] > filterItem.param.info.values[0];
                    break;
                case "before" :
                    result = item[filterItem.field.info.name] < filterItem.param.info.values[0];
                    break;
                case "in" :
                    result = false;
                    filterItem.param.info.values.forEach(function (value) {
                        if (item[filterItem.field.info.name] == value) {
                            result = true;
                        }
                    });

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