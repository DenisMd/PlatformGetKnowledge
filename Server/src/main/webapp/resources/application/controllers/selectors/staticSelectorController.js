model.controller("staticSelectorController" , function ($scope , customFilterService) {
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


    $scope.filterSearch = function(item)
    {
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
                case "checkBox":
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
    };

    $scope.openCustomFilter = function(){
        customFilterService.openDialog();
    }
});