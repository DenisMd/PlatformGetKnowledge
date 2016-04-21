model.controller("listDialogController",function($scope,listDialogService,$filter){
    var field           = null;
    var currentElement  = null;

    $scope.modalModel = null;

    $scope.resetActiveElementInModal = function(){
        if (currentElement) {
            currentElement.removeClass("info");
            currentElement = null;
        }
    };

    $scope.closeModal = function(){
        listDialogService.closeDialog();
        $scope.resetActiveElementInModal();
        $scope.modalModel       = "";
        $scope.selectModalValue = null;
        listDialogService.modelOpen(false);
    };

    $scope.setModalModel = function(event,value){
        $scope.resetActiveElementInModal();
        var elem = angular.element(event.currentTarget);
        currentElement = elem;
        elem.addClass("info");
        $scope.selectModalValue = value;
    };

    $scope.onEvent = function (event) {
        var elem = angular.element(event.currentTarget);
        switch (event.type) {
            case "mouseenter":
                elem.addClass("active");
                break;
            case "mouseleave":
                elem.removeClass("active");
                break;
        }
    };


    $scope.getItems = function () {
        var list = listDialogService.getListInfo().list;
        var data = {};

        field = listDialogService.getListInfo().field;

        if (!field) {
            data = $scope.modalModel;
        } else {
            data[field] = $scope.modalModel;
        }
        var filteredData = $filter('filter')(list,data);

        if (listDialogService.modelOpen()) {
            if (filteredData.length === 0) {
                $scope.selectForm['search-input'].$setValidity("searchValue", false);
            } else {
                $scope.selectForm['search-input'].$setValidity("searchValue", true);
            }
        }

        return filteredData;
    };

    //scroll для таблицы
    $scope.selectScrollConfig = angular.merge({setHeight: listDialogService.height()}, $scope.modalScrollConfig);

});