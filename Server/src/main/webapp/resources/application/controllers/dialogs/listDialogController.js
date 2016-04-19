model.controller("listDialogController",function($scope,listDialogService,$filter){

    var isModelOpen = false;
    var field       = listDialogService.getListInfo().field;
    var modalId     = "listDialogId";


    $scope.modalModel = null;

    $scope.$on("openListDialogModalEvent",function(event){
        $(modalId).modal({
            backdrop: 'static',
            keyboard: false
        });
        $(modalId).modal('show');
        $(modalId+" .table-content").height(getHeight());
    });

    $scope.closeModal = function(){
        $(modalId).modal("hide");
        $scope.resetActiveElementInModal();
        $scope.modalModel       = "";
        $scope.selectModalValue = null;
        isModelOpen             = false;
    };


    $scope.getData = function () {
        var list = listDialogService.getListInfo().list;

        var data = {};
        if (!field) {
            data = $scope.modalModel;
        } else {
            data[field] = $scope.modalModel;
        }
        var filteredData = $filter('filter')(list,data);

        if (isModelOpen) {
            if (filteredData.length === 0) {
                $scope.selectForm['search-input'].$setValidity("searchValue", false);
            } else {
                $scope.selectForm['search-input'].$setValidity("searchValue", true);
            }
        }

        return filteredData;
    };

});