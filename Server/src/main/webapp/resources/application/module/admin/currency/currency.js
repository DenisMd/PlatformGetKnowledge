model.controller("currencyCtrl", function ($scope,applicationService,className) {

    $scope.currentCurrency = null;

    //Данные для статического селектара
    $scope.selectorData = {
        list        : [],
        tableName   :   "currency",
        filters      : [
            {
                title : "name",
                type  : "text",
                field : "name",
                default : true
            },{
                title : "currency_char_code",
                type : "text",
                field : "charCode"
            },
            {
                title : "id",
                type  : "number",
                field : "id"
            },
            {
                title : "currency_is_base",
                type : "check_box",
                field : "baseCurrency",
                default : true
            },
            {
                title : "currency_value",
                type : "number",
                field : "value"
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            },
            {
                name : "name",
                orderBy : true
            },
            {
                name : "charCode",
                title : "currency_char_code",
                orderBy : true
            },
            {
                name : "baseCurrency",
                title : "currency_is_base",
                orderBy : true
            },
            {
                name : "value",
                title : "currency_value"
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentCurrency = item;
        },
        actions : [
            {
                icon : "fa-rub",
                color : "#15206C",
                tooltip : "currency_update",
                actionCallback : function (ev){
                    applicationService.action($scope,"" , className.currency,"updateCurrency",{},function(result){
                        $scope.showToast($scope.getResultMessage(result));
                        currencyList();
                    });
                }
            }
        ]
    };

    function currencyList() {
        $scope.selectorData.list = [];
        //Список валют доступных в системе
        applicationService.list($scope , "currency",className.currency,function (item) {
            $scope.selectorData.list.push(item);
        });
    }

    currencyList();

});