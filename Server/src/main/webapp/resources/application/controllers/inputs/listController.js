//список элементов для выбора
model.controller("listController",function($scope,listDialogService,$filter) {

    //Свойство, которое будет использоваться для заголовка моделей
    $scope.titleField = $scope.getData().titleField;

    //Данная опция отвечает за показ списка элементов под строкой ввода
    $scope.isShowSelectOptions  = false;

    //Выбранный объект из списка
    $scope.selectedItem   = null;

    //Класс для input-group
    $scope.classForInput         = $scope.getData().classForInput ? $scope.getData().classForInput : "input-group-lg";

    //В данной модели будут хранится отфильтрованные данные из списка
    $scope.filtredList = [];

    var count = $scope.getData().count;

    //Функция которая будет вызываться при выборе элемента из списка
    var callback            = angular.isFunction($scope.getData().callback)? $scope.getData().callback : null;


    //-------------------------------------------------------------------------- Events
    //Сбрасывает состояние модели
    if ($scope.getData().id != null) {
        $scope.$on('reset' + $scope.getData().id.capitalizeFirstLetter() + 'Event', function (event, args) {
            $scope.selectedItem = null;
            $scope.isShowSelectOptions = false;
        });
    }

    //-------------------------------------------------------------------------- Методы

    //получение списка из scope
    function getList(){
        return $scope.getData().listName in $scope ? $scope[$scope.getData().listName] : [];
    }

    //открыть диалог
    $scope.openDialog = function(){
        listDialogService.setListInfo({
            list        : getList(),
            maxHeight   : $scope.getData().maxHeigt
        });
        listDialogService.openDialog();
    };

    //При выборе элемента
    $scope.setItem = function (value) {
        //Делаем копию объекта
        $scope.selectedItem = value;
        $scope.isShowSelectOptions  = false;
        callback(value);
    };

    //При нажатие клавиши в input[main-select]
    $scope.onKeyUp = function(){
        $scope.isShowSelectOptions = true;
        $scope.filtredList = $scope.getFilteredData();
    };

    $scope.getFilteredData = function() {
        var list    = getList();
        var filter  = {};
        filter[$scope.titleField] = $scope.selectedItem ? $scope.selectedItem[$scope.titleField] : "";

        console.log(list);

        var filteredData = $filter('filter')(list, filter);
        filteredData = $filter('limitTo')(filteredData, count);

        var valid = false;
        if (filteredData.length >= 1) {
            if ($scope.selectedItem && $scope.selectedItem[$scope.titleField].toString() === filteredData[0][$scope.titleField].toString()) {
                valid = true;
            }
        }

        console.log(valid);
        $scope.selectForm['main-select'].$setValidity("selectValue", valid);
        $scope.setValid();

        return filteredData;
    };

    //закрытие подсказки, если щелчок происходдит за пределами элемнта
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