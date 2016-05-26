model.controller("foldersController" , function ($scope,applicationService) {
    var filter = applicationService.createFilter($scope.getData().className,0,10);
    filter.createFiltersInfo();
    filter.equals("section.name","text",$scope.getData().sectionName);
    $scope.folders = [];

    var addLog = function(folder){
        $scope.folders.push(folder);
    };

    var doAction = function(){
        applicationService.filterRequest($scope,"",filter,addLog);
    };

    doAction();

    $scope.loadMore = function () {
        filter.increase(10);
        doAction();
    };

    $scope.folderImg = function(id){
        return applicationService.imageHref($scope.getData().className,id);
    };

    //TODO: убрать
    $scope.splitArray = function(array,even) {
        var tempArr = [];
        for (var i = 0; i < array.length; i++) {
            if(i % 2 === 0 && even) { // index is even
                tempArr.push(array[i]);
            }
            if(i % 2 === 1 && !even) { // index is onn
                tempArr.push(array[i]);
            }
        }
        return tempArr;
    };
});