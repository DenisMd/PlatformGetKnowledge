var TYPES = {
    Field       : "Field",
    Operation   : "Operation",
    Parameter   : "Parameter"
};

//info: {name }
function filterItem(type , info){
    this.type = type;
    this.info = info;
}


model.controller("customFilterController",function($scope,customFilterService){

    $scope.filterRequest = [];

    function isLogicalOperation(item){
        return item.info.name == "And" || item.info.name == "Or";
    }

    $scope.addField = function(field){
        var length = $scope.filterRequest.length;
        //Добавляем филд в запрос фильтра если в запросе ничего нет или он идет после логической операции
        if (length == 0 || isLogicalOperation($scope.filterRequest[length-1]) ) {
            $scope.filterRequest.push(field);
        } else if ($scope.filterRequest[length-1].type == TYPES.Field) {
            //изменяем последний филд
            $scope.filterRequest[length-1] = field;
        }
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