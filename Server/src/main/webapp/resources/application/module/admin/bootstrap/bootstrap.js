model.controller("bootstrapCtrl", function ($scope,applicationService,className,$mdDialog,$mdMedia) {

    $scope.currentService = null;

    //Данные для статического селектара
    $scope.selectorData = {
        list        : [],
        tableName   :   "bootstrap_services",
        filters      : [
            {
                title : "name",
                type  : "text",
                field : "name",
                default : true
            },
            {
                title : "id",
                type  : "number",
                field : "id"
            },
            {
                title : "bootstrap_repeat",
                type : "checkBox",
                field : "repeat",
                default : true
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
                name : "bootstrapState",
                title : "bootstrap_state",
                orderBy : true
            },
            {
                name : "order",
                title : "bootstrap_order",
                orderBy : true
            },
            {
                name : "repeat",
                title : "bootstrap_repeat"
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentService = item;
        }
    };   
    
    
    //Список сервисов для которых выполняется bootstrap опции
    applicationService.list($scope , "bootstrap_services",className.bootstrap_services,function (service) {
        $scope.selectorData.list.push(service);
    });

    //Обновляем информацию о сервисе
    $scope.updateService = function() {
      applicationService.update($scope,"updateResult",className.bootstrap_services,$scope.currentService,function(result){
         $scope.showToast(result);
      });
    };


    $scope.showBootstrapDialog = function(ev) {
        $scope.showDialog(ev,$scope,"doBootstrapModal.html",function(answer){
            applicationService.action($scope,"bootstrapResult" , className.bootstrap_services,"do",answer,function(result){
                $scope.showToast(result);
                applicationService.list($scope , "bootstrap_services",className.bootstrap_services);
            });
        });
    };

});