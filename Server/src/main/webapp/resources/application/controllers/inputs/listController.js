//список элементов для выбора
model.controller("listController",function($scope,$sce,$filter) {

    var isModelOpen = false;

    $scope.choose           = false;
    $scope.modalModel       = null;
    $scope.selectModalValue = null;
    $scope.selectValue      = null;
    $scope.list             = [];

    $scope.model            = $scope.getData().defaultValue in $scope ? $scope[$scope.getData().defaultValue] : "";
    $scope.filter           = $scope.getData().filter;
    $scope.id               = $scope.getData().id;
    $scope.count            = $scope.getData().count;
    $scope.class            = $scope.getData().class;
    $scope.callback         = angular.isFunction($scope.getData().callback)? $scope.getData().callback : null;

    var selector            = '#' + $scope.id;

    $scope.closeModal = function(){
        $(selector).modal("hide");
        $scope.resetActiveElementInModal();
        $scope.modalModel = "";
        $scope.selectModalValue = null;
        isModelOpen = false;
    } ;
    $scope.getItem = function (item) {
        if (!item) {
            return "";
        }
        if (item.$$unwrapTrustedValue) {
            return item;
        } else {
            return item[$scope.filter];
        }
    };

    $scope.getList = function(){
        $scope.list = $scope.getData().listName in $scope ? $scope[$scope.getData().listName] : [];
        return $scope.list;
    };

    $scope.getFilteredData = function (isModal) {
        var list = $scope.getList();

        var filter = {};
        if (!$scope.filter) {
            filter = isModal?$scope.modalModel:$scope.model;
        } else {
            filter[$scope.filter] = isModal?$scope.modalModel:$scope.model;
        }
        var filteredData = $filter('filter')(list,filter);
        if (!isModal) {
            filteredData = $filter('limitTo')(filteredData, $scope.count);
            var valid = true;
            if (filteredData) {
                if (filteredData.length === 1) {
                    if ($scope.choose && $scope.model.toString() === $scope.getItem(filteredData[0]).toString()) {
                        $scope.setModel(filteredData[0]);
                    }
                } else {
                    if (filteredData.length === 0) {
                        if ($scope.isRequired()) {
                            valid = false;
                        }
                    }
                }
            }
            if (!$scope.choose && $scope.model && $scope.model.toString() !== $scope.getItem(filteredData[0]).toString()){
                valid = false;
            }
            $scope.selectForm['main-select'].$setValidity("selectValue", valid);
            $scope.setValid();
        } else {
            if (isModelOpen) {
                if (filteredData.length === 0) {
                    $scope.selectForm['search-input'].$setValidity("searchValue", false);
                } else {
                    $scope.selectForm['search-input'].$setValidity("searchValue", true);
                }
            }
        }
        return filteredData;
    };

    $scope.empty = function(isModal){
        return $scope.getFilteredData(isModal).length === 0;
    };

    $scope.setSelect = function (value) {
        $scope.choose = value;
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

    $scope.resetModel = function () {
        $scope.model = "";
        $scope.selectValue = null;
        $scope.choose = false;
    };

    $scope.setModel = function (value) {
        $scope.model = getValue(value);
        $scope.selectValue = value;
        $scope.choose = false;
        $scope.callback(value);
    };

    $scope.open = function () {
        $(selector).modal({
            backdrop: 'static',
            keyboard: false
        });
        $(selector).modal('show');
        $(selector+" .table-content").height(getHeight());
        isModelOpen = true;
    };

    var currentElement;
    $scope.resetActiveElementInModal = function(){
        if (currentElement) {
            currentElement.removeClass("info");
            currentElement = null;
        }
    };

    $scope.setModalModel = function(event,value){
        $scope.resetActiveElementInModal();
        var elem = angular.element(event.currentTarget);
        currentElement = elem;
        elem.addClass("info");
        $scope.selectModalValue = value;
    };

    $scope.saveModalModel = function(){
        $('#' + $scope.id).modal('hide');
        $scope.setModel($scope.selectModalValue);
        $scope.resetActiveElementInModal();
    };

    //закрытие подсказки, если щелчок происходдит за пределами элемнта
    $scope.hideSelect = function(){
        $scope.$apply(function () {
            if ($scope.choose) {
                $scope.getFilteredData();
                $scope.choose = false;
            }
        });
    };

    $scope.isRequired = function(){
        var val = $scope.getData().required;

        if (!val) {
            return false;
        }

        if (angular.isFunction(val)){
            return val();
        } else{
            return val;
        }
    };

    //есть ли запрет на редактирование
    $scope.isDisabled = function(){
        var val = $scope.getData().disable;

        if (!val) {
            return false;
        }

        if (angular.isFunction(val)){
            return val();
        } else{
            return val;
        }
    };

    $scope.setValid = function(){
        var val = $scope.getData().isValid;

        if (!val) {
            return false;
        }
        if (angular.isFunction(val)) {
            val($scope.selectForm['main-select'].$valid);
        }
    };

    //scroll для таблицы
    $scope.selectScrollConfig = angular.merge({setHeight: getHeight()}, $scope.modalScrollConfig);

    //ожидание сброса значения
    $scope.$on('reset'+$scope.id.capitalizeFirstLetter()+'Event', function(event, args) {
        $scope.resetModel();
    });

    //подсчет высоты основного содержания модалки
    function getHeight(){
        var height = $scope.getData().maxHeight? $scope.getData().maxHeight: 400;
        var temp = 40 * $scope.getList().length;
        return !temp || temp > height? height : temp;
    }

    //текст отображающийся в input
    function getValue(value){
        if (angular.isString(value) || value.$$unwrapTrustedValue) {
            return value;
        } else {
            return value[$scope.filter];
        }
    }

});