model.controller("foldersController" , function ($scope,applicationService) {

    $scope.currentFilterByDate = true;

    $scope.filter = applicationService.createFilter($scope.getData().className,0,10);
    $scope.filter.createFiltersInfo();
    $scope.filter.equals("section.name","text",$scope.getData().sectionName);

    $scope.by_date = function() {
        $scope.currentFilterByDate = true;
        $scope.filter.clearOrder();
        $scope.filter.clearCustomFilters();
        $scope.filter.setOrder("createDate" , true);

        $scope.folders = [];

        doAction();
    };

    $scope.by_count = function() {
        $scope.currentFilterByDate = false;
        $scope.filter.clearOrder();
        $scope.filter.clearCustomFilters();
        $scope.filter.addCustomFilter("orderByCount" , {});

        $scope.folders = [];

        doAction();
    };
    var likeIndex;
    $scope.searchFilter = function(text) {
        if (likeIndex != undefined) {
            $scope.filter.result.filtersInfo.filters.splice(likeIndex,1);
        }
        if (text) {
            likeIndex = $scope.filter.like("title", "text", "%" + text + "%");
        }

        $scope.folders = [];

        doAction();
    };

    var addLog = function(folder){
        folder.imgSrc = applicationService.imageHref($scope.getData().className,folder.id);
        $scope.folders.push(folder);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"foldersInfo",$scope.filter,addLog);
    };

    $scope.by_date();

    $scope.loadMore = function () {
        $scope.filter.increase(10);
        doAction();
    };
});