var TYPES = {
    Field       : "Field",
    Operation   : "Operation",
    Parameter   : "Parameter"
};

//info: {name,type,constants} - Field
//info: {name , symbol} - Operation
//info: {values = [,,]} - Parameters
function filterItem(type , info){
    this.type = type;
    this.info = info;
}

/**
 *  @param{filterItem} field
    @param{filterItem} oper
    @param{filterItem} param
 * */

function filterExpression(field,oper,param) {
    this.field = field;
    this.oper = oper;
    this.param = param;
}


model.controller("customFilterController",function($scope,customFilterService){

    var STATE = {
        FIELD : 0,
        OPERATION : 1,
        SET_VALUE : 2
    };

    $scope.state = STATE.FIELD;
    $scope.isUpdate = false;
    $scope.currentValue = null;
    
    $scope.setCurrentValue = function (value) {
        $scope.currentValue = value;
    };
    

    //Возврат по состаянию
    $scope.back = function () {
        switch ($scope.state){
            case STATE.FIELD :
                return;
            case STATE.OPERATION :
                backToFiled();
                break;
            case STATE.SET_VALUE:
                backToOperation();
                break;
        }
        $scope.state = --$scope.state % Object.keys(STATE).length;
    };

    $scope.$watch("selectedOperation.value" , function (newValue) {
        if (newValue) {
            $scope.currentValue = newValue;
        }
    });

    //Следующие состаяние
    $scope.next = function () {
        $scope.isUpdate = true;
        switch ($scope.state){
            case STATE.FIELD :
                addField($scope.currentValue);
                break;
            case STATE.OPERATION :
                addOperation();
                switch ($scope.currentFilterExpression.field.info.type){
                    case "text" :
                        if ($scope.selectedOperation.value.name  === 'in'){
                            $scope.inParams = [{}];
                        } else {
                            $scope.params = {};
                        }
                        break;
                    case "number" :
                        if ($scope.selectedOperation.value.name === 'between'){
                            $scope.params = {};
                        } else {
                            if ($scope.selectedOperation.value.name === 'in'){
                                $scope.inParams = [{}];
                            } else {
                                $scope.params = {};
                            }
                        }
                        break;
                    case "dateTime" :
                        break;
                    case "enum" :
                        if ($scope.selectedOperation.value.name === 'in'){
                            $scope.inParams = [{}];
                        } else {
                            $scope.params = {};
                        }
                        break;
                    case "check_box" :
                        $scope.params = {
                            check : false
                        };
                        break;
                }
                break;
            case STATE.SET_VALUE:

                switch ($scope.currentFilterExpression.field.info.type){
                    case "text" :
                        if ($scope.selectedOperation.value.name  === 'in'){
                            $scope.createFilterExpressionFromArray($scope.inParams);
                            delete $scope.inParams;
                        } else {
                            $scope.createFilterExpression($scope.params.text);
                            delete $scope.params;
                        }
                        break;
                    case "number" :
                        if ($scope.selectedOperation.value.name === 'between'){
                            $scope.createFilterExpression($scope.params.number1, $scope.params.number2);
                            delete $scope.params;
                        } else {
                            if ($scope.selectedOperation.value.name === 'in'){
                                $scope.createFilterExpressionFromArray($scope.inParams);
                                delete $scope.inParams;
                            } else {
                                $scope.createFilterExpression($scope.params.number);
                                delete $scope.params;
                            }
                        }
                        break;
                    case "dateTime" :
                        $scope.createDateExpression();
                        break;
                    case "enum" :
                        if ($scope.selectedOperation.value.name === 'in'){
                            $scope.createFilterExpressionFromArray($scope.inParams);
                            delete $scope.inParams;
                        } else {
                            $scope.createFilterExpression($scope.params.enum);
                            delete $scope.params.enum;
                        }
                        break;
                    case "check_box" :
                        $scope.createFilterExpression($scope.params.check);
                        delete $scope.params;
                }
                break;
        }
        $scope.currentValue = null;
        $scope.state = ++$scope.state % Object.keys(STATE).length;
        $scope.isUpdate = false;
        console.log($scope.params);
    };


    $scope.isDisabled = function () {
        if ($scope.state == STATE.SET_VALUE){
            switch ($scope.currentFilterExpression.field && $scope.currentFilterExpression.field.info.type){
                case "text" :
                    if ($scope.selectedOperation.value.name  !== 'in'){
                        return angular.isUndefined($scope.params.text);
                    }
                    break;
                case "number" :
                    if ($scope.selectedOperation.value.name === 'between'){
                        return angular.isUndefined($scope.params.number1) || angular.isUndefined($scope.params.number2) || $scope.params.number1 >= $scope.params.number2
                    } else {
                        if ($scope.selectedOperation.value.name !== 'in'){
                            return angular.isUndefined($scope.params.number);
                        }
                    }
                    break;
                case "dateTime" :
                    if ($scope.selectedOperation.value.name === 'between'){
                       return angular.isUndefined($scope.dateParam1) || angular.isUndefined($scope.dateParam2);
                    } else {
                        return angular.isUndefined($scope.dateParam1);
                    }
                    break;
                case "enum" :
                    if ($scope.selectedOperation.value.name !== 'in'){
                        return angular.isUndefined($scope.params.enum);
                    }
                    break;
                case "check_box" :
                    return angular.isUndefined($scope.params.check);
            }
        }
        return false;
    };

    $scope.logicalExpression = "and";

    $scope.operationMap = {
        text : {
            values : [
                {
                    name : "equals",
                    symbol : "="
                },{
                    name : "like",
                    symbol : "like"
                },{
                    name : "in",
                    symbol : "in"
                }
            ]
        },
        number : {
            values : [
                {
                    name : "equals",
                    symbol : "="
                },{
                    name : "great_than",
                    symbol : ">"
                },{
                    name : "great_than_or_equal_to",
                    symbol : ">="
                },{
                    name : "less_than",
                    symbol : "<"
                },{
                    name : "less_than_or_equal",
                    symbol : "<="
                },{
                    name : "between",
                    symbol : "between"
                },{
                    name : "in",
                    symbol : "in"
                }

            ],
        },
        dateTime : {
            values : [
                {
                    name : "after",
                    symbol : "after"
                },{
                    name : "before",
                    symbol : "before"
                },{
                    name : "between",
                    symbol : "between"
                }
            ]
        },
        enum : {
          values : [
              {
                  name : "equals",
                  symbol : "="
              },
              {
                  name : "in",
                  symbol : "in"
              }
          ]
        },
        other : {
            values : [
                {
                    name : "equals",
                    symbol : "="
                }
            ]
        }
    };

    //Выбранная операция
    $scope.selectedOperation = {};
    
    //Массив из filterExpressions
    $scope.filterRequest = [];

    $scope.isParamsInput = false;
    
    $scope.currentFilterExpression = new filterExpression(null,null,null);

    //Добавление поля в фильтр
     function addField(field){
        if (!$scope.isParamsInput) {
            $scope.currentFilterExpression.field = new filterItem(TYPES.Field, {name: field.field, type: field.type , constants : field.constants});
            $scope.selectedOperation = {};
        }
    };

    //Возврат к выбору поля
    function backToFiled() {
        $scope.currentFilterExpression.field = null;
    };

    //Добовление операции в фильтр
     function addOperation() {
        $scope.currentFilterExpression.oper = new filterItem(TYPES.Operation,{
            name : $scope.selectedOperation.value.name,
            symbol : $scope.selectedOperation.value.symbol});
        $scope.isParamsInput = true;
    };

    function backToOperation() {
        $scope.currentFilterExpression.oper = null;
        $scope.isParamsInput = false;
    };

    $scope.createFilterExpression = function(param1,param2) {
        var paramValues = [param1];
        if (param2) {paramValues.push(param2);}
        $scope.currentFilterExpression.param = new filterItem(TYPES.Parameter,{
           values : paramValues
        });
        $scope.filterRequest.push($scope.currentFilterExpression);
        $scope.currentFilterExpression = new filterExpression(null,null,null);
        $scope.isParamsInput = false;
    };
    
    $scope.createFilterExpressionFromArray = function (params) {
        var tempArr = [];

        params.forEach(function (item) {
            tempArr.push(item.text);
        });

        $scope.currentFilterExpression.param = new filterItem(TYPES.Parameter,{
            values : tempArr
        });
        $scope.filterRequest.push($scope.currentFilterExpression);
        $scope.currentFilterExpression = new filterExpression(null,null,null);
        $scope.isParamsInput = false;
    };

    $scope.createDateExpression = function() {
        var paramValues = [$scope.dateParam1];
        if ($scope.dateParam2) {paramValues.push($scope.dateParam2);}
        $scope.currentFilterExpression.param = new filterItem(TYPES.Parameter,{
            values : paramValues
        });
        $scope.filterRequest.push($scope.currentFilterExpression);
        $scope.currentFilterExpression = new filterExpression(null,null,null);
        $scope.isParamsInput = false;
        $scope.dateParam1 = undefined;
        $scope.dateParam2 = undefined;
    };

    $scope.clearFilter = function () {
        $scope.filterRequest = [];
    };

    $scope.removeFromRequest = function(index) {
        $scope.filterRequest.splice(index,1);
    };

    //Вызывается при отрытие диалога
    customFilterService.setCallbackOpen(function(defaultFilter){
        if (defaultFilter) {
            $scope.logicalExpression = defaultFilter.logicalOperation;
            $scope.filterRequest = angular.copy(defaultFilter.filterData);
        }
        $scope.filtersInfo = customFilterService.filtersInfo();
    });

    //Сохраняем структуру фильтра для исполнения
    $scope.saveFilter = function(){
        customFilterService.saveDialog({
            logicalOperation : $scope.logicalExpression,
            filterData : angular.copy($scope.filterRequest)
        });
    };

    //Закрываем модель без примения фильтра
    $scope.closeModal = function(){
        customFilterService.closeDialog();
    };

    $scope.dateTimeOptions1 = {
        id : "param1",
        format : "DD-MMMM-YYYY-HH-mm",
        minView : 'minute' ,
        startView : 'year',
        onChange: function(date,isValid){
            if (isValid) {
                $scope.dateParam1 = date;
            } else {
                $scope.dateParam1 = undefined;
            }
        }
    };

    $scope.dateTimeOptions2 = {
        id : "param2",
        format : "DD-MMMM-YYYY-HH-mm",
        minView : 'minute' ,
        startView : 'year',
        onChange: function(date,isValid){
            if (isValid) {
                $scope.dateParam2 = date;
            } else {
                $scope.dateParam2 = undefined;
            }
        }
    };

    $scope.selectScrollConfig = angular.merge({setHeight: 300}, $scope.modalScrollConfig);
});