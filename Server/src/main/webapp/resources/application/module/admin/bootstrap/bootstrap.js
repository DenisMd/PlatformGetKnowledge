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
                type : "check_box",
                field : "repeat"
            },
            {
                title : "bootstrap_start_time",
                type : "dateTime",
                field : "startTime"
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
            },{
                name : "startTime",
                title : "bootstrap_start_time",
                filter : "date"
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentService = item;
            if ($scope.currentService != null && $scope.currentService.stackTrace != null) {
                $scope.stackTraceData.stack = $scope.currentService.stackTrace;
            }
        },
        actions : [
            {
                icon : "fa-cogs",
                color : "#15206C",
                tooltip : "bootstrap_do_bootstrap",
                actionCallback : function (ev){
                    //$event
                    $scope.showDialog(ev,$scope,"doBootstrapModal.html",function(answer){
                        applicationService.action($scope,"bootstrapResult" , className.bootstrap_services,"do",answer,function(result){
                            $scope.showToast($scope.getResultMessage(result));
                            updateList();
                        });
                    });
                }
            }
        ]
    };
    
    //Информация для вывода stack trace при ошибке выполнения сервиса
    $scope.stackTraceData = {
        title : "bootstrap_stackTrace"   
    };

    //Обновляем информацию о сервисе
    $scope.updateService = function() {
        applicationService.update($scope,"",className.bootstrap_services,$scope.currentService,function(result){
            $scope.showToast(result);
        });
    };

    function updateList() {
        $scope.selectorData.list = [];
        //Список сервисов для которых выполняется bootstrap опции
        applicationService.list($scope , "bootstrap_services",className.bootstrap_services,function (service) {
            $scope.selectorData.list.push(service);
        });
    }

    updateList();

});