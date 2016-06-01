model.controller("foldersController" , function ($scope,applicationService) {

    var numberFoldersInRow = 4;
    var tempArr = [];

    $scope.currentFilterByDate = true;

    $scope.filter = applicationService.createFilter($scope.getData().className,0,10);
    $scope.filter.createFiltersInfo();
    $scope.filter.equals("section.name","text",$scope.getData().sectionName);

    $scope.by_date = function() {
        $scope.currentFilterByDate = true;
        $scope.filter.clearOrder();
        $scope.filter.clearCustomFilters();
        $scope.filter.setOrder("createDate" , true);

        tempArr = [];
        $scope.foldersGroup = [tempArr];

        doAction();
    };

    $scope.by_count = function() {
        $scope.currentFilterByDate = false;
        $scope.filter.clearOrder();
        $scope.filter.clearCustomFilters();
        $scope.filter.addCustomFilter("orderByCount" , {});

        tempArr = [];
        $scope.foldersGroup = [tempArr];

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

        tempArr = [];
        $scope.foldersGroup = [tempArr];

        doAction();
    };

    var addLog = function(folder){
        folder.imgSrc = applicationService.imageHref($scope.getData().className,folder.id);
        if (tempArr.length == numberFoldersInRow) {
            tempArr = [];
            $scope.foldersGroup.push(tempArr);
        }

        tempArr.push(folder);
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