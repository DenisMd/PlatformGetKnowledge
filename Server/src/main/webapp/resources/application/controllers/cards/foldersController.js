model.controller("foldersController" , function ($scope,applicationService) {

    $scope.currentFilterByDate = false;

    $scope.filter = applicationService.createFilter($scope.getData().className,0,12);
    $scope.filter.setDistinct(false);
    $scope.filter.createFiltersInfo();
    $scope.filter.equals("section.name","text",$scope.getData().sectionName);

    $scope.by_date = function() {
        $scope.currentFilterByDate = true;
        $scope.filter.clearOrder();
        $scope.filter.clearCustomFilters();
        $scope.filter.setOrder("createDate" , true);

        $scope.filter.result.first = 0;
        $scope.folders = [];

        doAction();
    };

    $scope.by_count = function() {
        $scope.currentFilterByDate = false;
        $scope.filter.clearOrder();
        $scope.filter.clearCustomFilters();
        $scope.filter.addCustomFilter("orderByCount" , {});

        $scope.filter.result.first = 0;
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

        $scope.filter.result.first = 0;
        $scope.folders = [];

        doAction();
    };

    var addFolder = function(folder){
        if (folder == null) {
            return;
        }
        folder.imgSrc = applicationService.imageHref($scope.getData().className,folder.id);
        $scope.folders.push(folder);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"foldersInfo",$scope.filter,addFolder);
    };

    $scope.by_count();

    $scope.loadMore = function () {
        $scope.filter.increase(12);
        doAction();
    };
});