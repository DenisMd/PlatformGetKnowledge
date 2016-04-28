var TYPES = {
    Field       : "Field",
    Operation   : "Operation",
    Parameter   : "Parameter"
};

//info: {name,type} - Field
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
                }
            ]
        },
        number : {
            values : [
                {
                    name : "equals",
                    symbol : "="
                },{
                    name : "more",
                    symbol : ">"
                },{
                    name : "more_or_equal",
                    symbol : ">="
                },{
                    name : "less",
                    symbol : "<"
                },{
                    name : "less_or_equal",
                    symbol : ">"
                },{
                    name : "between",
                    symbol : "between"
                }
            ],
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
    $scope.addField = function(field){
        if (!$scope.isParamsInput) {
            $scope.currentFilterExpression.field = new filterItem(TYPES.Field, {name: field.field, type: field.type});
            $scope.selectedOperation = {};
        }
    };

    //Добовление операции в фильтр
    $scope.addOperation = function () {
        $scope.currentFilterExpression.oper = new filterItem(TYPES.Operation,{
            name : $scope.selectedOperation.value.name,
            symbol : $scope.selectedOperation.value.symbol});
        $scope.isParamsInput = true;
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

    $scope.clearFilter = function () {
        $scope.filterRequest = [];
    };

    $scope.removeFromRequest = function(index) {
        $scope.filterRequest.splice(index,1);
    };

    //Вызывается при отрытие диалога
    customFilterService.setCallbackOpen(function(){
        $scope.filtersInfo = customFilterService.filtersInfo();
    });

    //Сохраняем структуру фильтра для исполнения
    $scope.saveFilter = function(){
        customFilterService.saveDialog($scope.filterRequest);
    };

    //Закрываем модель без примения фильтра
    $scope.closeModal = function(){
        customFilterService.closeDialog();
    };
});