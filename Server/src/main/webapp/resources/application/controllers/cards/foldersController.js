model.controller("foldersController" , function ($scope,className,applicationService,$state) {

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

    //Проверка на существование секции
    var folderInfo = applicationService.createFilter(className.section,0,10);
    folderInfo.createFiltersInfo();
    folderInfo.equals("name","text",$scope.getData().sectionName);
    applicationService.filterRequest($scope,"sectionInfo", folderInfo,function(section){
        if (section == null) {
            $state.go("404");
        }

        $scope.by_count();
    });

    $scope.loadMore = function () {
        $scope.filter.increase(12);
        doAction();
    };
});