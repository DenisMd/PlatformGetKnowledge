model.controller("foldersController" , function ($scope,className,applicationService,$state) {
    
    $scope.filter = applicationService.createFilter($scope.getData().className,0,12);
    $scope.filter.setDistinct(false);
    $scope.filter.createFiltersInfo();
    $scope.orderDesc = true;
    $scope.filter.equals("section.name","text",$scope.getData().sectionName);

    $scope.by_date = function(orderDesc) {
        $scope.filter.clearOrder();
        $scope.filter.clearCustomFilters();
        $scope.filter.setOrder("createDate" , orderDesc);

        $scope.filter.result.first = 0;
        $scope.folders = [];

        doAction();
    };

    $scope.by_count = function(orderDesc) {
        $scope.filter.clearOrder();
        $scope.filter.clearCustomFilters();
        $scope.filter.addCustomFilter("orderByCount" , {
            desc : orderDesc
        });

        $scope.filter.result.first = 0;
        $scope.folders = [];

        doAction();
    };

    $scope.currentFilter = "1";
    $scope.sortings = [
        {
            id : "1",
            title : "by_count",
            callback : function() {
                $scope.currentFilter = this.id;
                $scope.orderDesc = !$scope.orderDesc;
                $scope.by_count($scope.orderDesc);
            }
        },{
            id : "2",
            title : "by_date",
            callback : function() {
                $scope.currentFilter = this.id;
                $scope.orderDesc = !$scope.orderDesc;
                $scope.by_date($scope.orderDesc);
            }
        }
    ];

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

        $scope.by_count($scope.orderDesc);
    });

    $scope.loadMore = function () {
        $scope.filter.increase(12);
        doAction();
    };
});