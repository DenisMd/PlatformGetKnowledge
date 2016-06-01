model.controller("foldersController" , function ($scope,applicationService) {

    var numberFoldersInRow = 4;

    $scope.filter = applicationService.createFilter($scope.getData().className,0,10);
    $scope.filter.createFiltersInfo();
    $scope.filter.equals("section.name","text",$scope.getData().sectionName);

    //filter.addCustomFilter("orderByCount" , {});

    var tempArr = [];
    $scope.foldersGroup = [tempArr];


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

    doAction();

    $scope.loadMore = function () {
        $scope.filter.increase(10);
        doAction();
    };
});