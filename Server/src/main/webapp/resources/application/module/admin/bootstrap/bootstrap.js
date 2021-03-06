model.controller("bootstrapCtrl", function ($scope,applicationService,className) {

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
                title : "bootstrap_state",
                type : "enum",
                field : "bootstrapState",
                constants : [
                    {
                        key : "NotComplete",
                        value : "bootstrap_not_complete"
                    } , {
                        key : "Completed",
                        value : "bootstrap_completed"
                    } , {
                        key : "Failed",
                        value: "bootstrap_failed"
                    }],
                default : true
            },
            {
                title : "bootstrap_repeat",
                type : "check_box",
                field : "repeat",
                default : true
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
                orderBy : true,
                defaultOrder : true,
                desc : false
            },
            {
                name : "name",
                orderBy : true
            },
            {
                name : "translatedState",
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
            if ($scope.currentService.stackTrace) {
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
                            bootstrapInfoList();
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
            $scope.showToast($scope.getResultMessage(result));
        });
    };

    function bootstrapInfoList() {
        $scope.selectorData.list = [];
        //Список сервисов для которых выполняется bootstrap опции
        applicationService.list($scope , "bootstrap_services",className.bootstrap_services,function (service) {
            switch (service.bootstrapState) {
                case "NotComplete" :
                    service.translatedState = $scope.translate("bootstrap_not_complete");
                    break;
                case "Completed" :
                    service.translatedState = $scope.translate("bootstrap_completed");
                    break;
                case "Failed" :
                    service.translatedState = $scope.translate("bootstrap_failed");
                    break;
            }
            $scope.selectorData.list.push(service);
        });
    }

    bootstrapInfoList();

});