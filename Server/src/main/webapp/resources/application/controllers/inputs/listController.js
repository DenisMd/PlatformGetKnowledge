//список элементов для выбора
model.controller("listController",function($scope,listDialogService,$filter) {

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!
    var field           = $scope.getData().field;

    //Данная опция отвечает за показ списка элементов под строкой ввода
    //!!!!!!!!
    $scope.isShowSelectOptions  = false;


    $scope.selectModalValue     = null;
    $scope.selectValue          = null;

    //Если задано значение по умолчанию устанавливаем его для модели
    $scope.selectedItem       = $scope.getData().defaultValue ? $scope.getData().defaultValue : "";

    //Еласс для input-group
    $scope.cssClass         = $scope.getData().cssClass ? $scope.getData().cssClass : "input-group-lg";

    //Функция которая будет вызываться при выборе элемента из списка
    var callback            = angular.isFunction($scope.getData().callback)? $scope.getData().callback : null;


    //-------------------------------------------------------------------------- Events
    //Сбрасывает состояние модели
    if ($scope.getData().id != null) {
        $scope.$on('reset' + $scope.getData().id.capitalizeFirstLetter() + 'Event', function (event, args) {
            $scope.selectedItem = "";
            $scope.selectValue = null;
            $scope.isShowSelectOptions = false;
        });
    }

    //-------------------------------------------------------------------------- Методы

    //получение списка из scope
    $scope.getList = function(){
        return $scope.getData().listName in $scope ? $scope[$scope.getData().listName] : [];;
    };

    //открыть диалог
    $scope.openDialog = function(){
        listDialogService.setListInfo({
            list : $scope.getList(),
            maxHeight : $scope.getData().maxHeigt,
            length : $scope.getData().length,
            field : $scope.getData().field
        });
        listDialogService.openDialog();
    };



    $scope.getItem = function (item) {
        if (!item) {
            return "";
        }
        if (item.$$unwrapTrustedValue) {
            return item;
        } else {
            return item[field];
        }
    };



    $scope.empty = function(isModal){
        return $scope.getFilteredData(isModal).length === 0;
    };

    $scope.setItem = function (value) {
        $scope.selectedItem = getTitle(value);
        $scope.selectValue = value;
        $scope.isShowSelectOptions = false;
        callback(value);
    };

    $scope.saveModalModel = function(){
        $('#' + $scope.id).modal('hide');
        $scope.setItem($scope.selectModalValue);
        $scope.resetActiveElementInModal();
    };

    $scope.getFilteredData = function() {
        var list = $scope.getList();

        var filter = {};

        if (!field) {
            filter = $scope.model;
        } else {
            filter[field] = $scope.model;
        }
        var filteredData = $filter('filter')(list, filter);
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
        if (!$scope.choose && $scope.model && $scope.model.toString() !== $scope.getItem(filteredData[0]).toString()) {
            valid = false;
        }
        $scope.selectForm['main-select'].$setValidity("selectValue", valid);
        $scope.setValid();

    };

    //закрытие подсказки, если щелчок происходдит за пределами элемнта
    //!!!!!!!!
    $scope.hideSelect = function(){
        $scope.$apply(function () {
            if ($scope.isShowSelectOptions) {
                $scope.getFilteredData();
                $scope.isShowSelectOptions = false;
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

    //текст отображающийся в input
    function getTitle(value){
        if (angular.isString(value) || value.$$unwrapTrustedValue) {
            return value;
        } else {
            return value[field];
        }
    }

});