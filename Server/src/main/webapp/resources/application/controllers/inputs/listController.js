//список элементов для выбора
model.controller("listController",function($scope,listDialogService,$sce) {


    var field           = $scope.getData().field;
    var currentElement  = null;

    //Данная опция отвечает за показ списка элементов под строкой ввода
    $scope.isShowSelectOptions  = false;


    $scope.selectModalValue     = null;
    $scope.selectValue          = null;
    $scope.list                 = [];

    //Если задано значение по умолчанию устанавливаем его для модели
    $scope.model            = $scope.getData().defaultValue ? $scope.getData().defaultValue : "";

    //Еласс для input-group
    $scope.cssClass         = $scope.getData().cssClass ? $scope.getData().cssClass : "input-group-lg";

    //Функция которая будет вызываться при выборе элемента из списка
    var callback            = angular.isFunction($scope.getData().callback)? $scope.getData().callback : null;


    //-------------------------------------------------------------------------- Events
    //Сбрасывает состояние модели
    if ($scope.getData().id != null) {
        $scope.$on('reset' + $scope.getData().id.capitalizeFirstLetter() + 'Event', function (event, args) {
            $scope.model = "";
            $scope.selectValue = null;
            $scope.isShowSelectOptions = false;
        });
    }

    //-------------------------------------------------------------------------- Методы


    $scope.openDialog = function(){
        listDialogService.setListInfo({
            list : $scope.getList()
        });
        $scope.$broadcast("openListDialogModalEvent");
    };

    $scope.resetActiveElementInModal = function(){
        if (currentElement) {
            currentElement.removeClass("info");
            currentElement = null;
        }
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

    $scope.getList = function(){
        $scope.list = $scope.getData().listName in $scope ? $scope[$scope.getData().listName] : [];
        return $scope.list;
    };

    $scope.empty = function(isModal){
        return $scope.getFilteredData(isModal).length === 0;
    };

    $scope.setSelect = function (value) {
        $scope.isShowSelectOptions = value;
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



    $scope.setModel = function (value) {
        $scope.model = getValue(value);
        $scope.selectValue = value;
        $scope.isShowSelectOptions = false;
        callback(value);
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

    //scroll для таблицы
    $scope.selectScrollConfig = angular.merge({setHeight: getHeight()}, $scope.modalScrollConfig);



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
            return value[field];
        }
    }

});