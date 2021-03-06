//список элементов для выбора
model.controller("listController",function($scope,listDialogService,$filter) {

    $scope.menuScrollConfig = {
        theme: 'light-3',
        snapOffset: 100,
        advanced: {
            updateOnContentResize: true,
            updateOnSelectorChange: "ul li"
        }
    };

    //Свойство, которое будет использоваться для заголовка моделей
    $scope.titleField = $scope.getData().titleField;

    //Данная опция отвечает за показ списка элементов под строкой ввода
    $scope.isShowSelectOptions  = false;

    //Выбранный объект из списка
    $scope.selectedItem = $scope.getData().defaultValue in $scope ? $scope[$scope.getData().defaultValue] : null;;

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
            $scope.selectedItem = $scope.getData().defaultValue in $scope ? $scope[$scope.getData().defaultValue] : null;
            $scope.isShowSelectOptions = false;
        });
    }

    //-------------------------------------------------------------------------- Методы

    //получение списка из scope
    function getList(){
        return $scope.getData().listName in $scope ? $scope[$scope.getData().listName] : [];
    }

    function getFilteredData() {
        var list    = getList();
        var filter  = {};
        filter[$scope.titleField] = $scope.selectedItem ? $scope.selectedItem[$scope.titleField] : "";

        var filteredData = $filter('filter')(list, filter);
        filteredData = $filter('limitTo')(filteredData, count);

        var valid = false;
        if (filteredData.length >= 1) {
            if ($scope.selectedItem && $scope.selectedItem[$scope.titleField]  && $scope.selectedItem[$scope.titleField] === filteredData[0][$scope.titleField]) {
                valid = true;
            }
        }
        
        $scope.selectForm['main-select'].$setValidity("selectValue", valid);
        checkValid(valid);

        return filteredData;
    }

    function checkValid(valid){
        var val = $scope.getData().valid;

        if (!val) {
            return;
        }

        if (angular.isFunction(val)) {
            val(valid);
        }
    }

    //открыть диалог
    $scope.openDialog = function(){
        listDialogService.setListInfo({
            list        : getList(),
            maxHeight   : $scope.getData().maxHeight,
            titleField  : $scope.titleField
        });
        //устанавливаем действие на выбор элемента из модального окна
        listDialogService.setCallbackSave(function(value){
            $scope.setItem(value);
        });
        listDialogService.openDialog();
    };

    //При выборе элемента
    $scope.setItem = function (value) {
        //Делаем копию объекта
        $scope.selectedItem = {};
        angular.extend($scope.selectedItem,value);
        //сбрасываем отфильтрованные значения и ставим валидацию
        getFilteredData();
        $scope.isShowSelectOptions  = false;
        callback(value);
    };

    //При нажатие клавиши в input[main-select]
    $scope.fillFiltredItem = function(){
        $scope.isShowSelectOptions = true;
        $scope.filtredList = getFilteredData();
    };



    //закрытие подсказки, если щелчок происходдит за пределами элемнта
    $scope.hideSelect = function(){
        $scope.$apply(function () {
            if ($scope.isShowSelectOptions) {
                getFilteredData();
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

});