model.controller("listDialogController",function($scope,listDialogService,$filter){

    var currentElement = null;

    //имя свойства которое будет показываться
    $scope.titleField   = null;

    //выбранный элемент
    $scope.currenItem = null;

    //Отсортированный список
    $scope.filtredList = [];

    function getItems() {
        var list = listDialogService.getListInfo().list;
        if (!list) return [];

        $scope.titleField = listDialogService.getListInfo().titleField;

        var data = {};
        data[$scope.titleField] = $scope.currenItem ? $scope.currenItem[$scope.titleField] : "";

        var filteredData = $filter('filter')(list,data);

        if (filteredData.length === 0) {
            $scope.selectForm['search-input'].$setValidity("searchValue", false);
        } else {
            $scope.selectForm['search-input'].$setValidity("searchValue", true);
        }


        return filteredData;
    }

    function resetActiveElementInModal(){
        if (currentElement) {
            currentElement.removeClass("info");
            currentElement = null;
        }
    }

    $scope.updateList = function () {
        $scope.filtredList = getItems();
    };

    listDialogService.setCallbackOpen(function(){
        $scope.currenItem = null;
        $scope.updateList();
        //scroll для таблицы
        $scope.selectScrollConfig = angular.merge({setHeight: listDialogService.height()}, $scope.modalScrollConfig);

    });


    $scope.empty = function(){
        return $scope.filtredList.length === 0;
    };

    //При сохранение выбранного элемента из списка
    $scope.saveModalModel = function(){
        listDialogService.closeDialog($scope.currenItem);
        resetActiveElementInModal();
    };

    //Закрытие дилога
    $scope.closeModal = function(){
        resetActiveElementInModal();
        listDialogService.closeDialog();
        $scope.currenItem = null;
    };

    $scope.setModalModel = function(event,value){
        resetActiveElementInModal();
        var elem = angular.element(event.currentTarget);
        currentElement = elem;
        elem.addClass("info");
        //Делаем копию объекта
        $scope.currenItem = {};
        angular.extend($scope.currenItem,value);
        //сбрасываем отфильтрованные значения и ставим валидацию
        getItems();
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

});