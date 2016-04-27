model.controller("customFilterController",function($scope,customFilterService){

    $scope.saveFilter = function(){

    };

    $scope.closeModal = function(){
        customFilterService.closeDialog();
    };
});