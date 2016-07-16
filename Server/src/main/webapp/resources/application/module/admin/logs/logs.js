model.controller("logsCtrl", function ($scope,className) {

    $scope.currentLog = null;

    //Информация для вывода stack trace при ошибке выполнения сервиса
    $scope.stackTraceData = {
        title : "log_stack_trace"
    };

    $scope.selectorData = {
        className   : className.trace,
        tableName   :   "logs",
        loadMoreTitle : "log_load_more",
        filters      : [
            {
                title : "log_message",
                type  : "text",
                field : "message"
            },
            {
                title : "id",
                type : "number",
                field : "id"
            },
            {
                title : "log_trace_level",
                type : "enum",
                field : "traceLevel",
                constants : [{
                    key : "Debug",
                    value : "log_debug"
                }, {
                    key : "Event",
                    value : "log_event"
                }, {
                    key : "Warning",
                    value : "log_warning"
                }, {
                    key : "Error",
                    value : "log_error"
                }, {
                    key : "Critical",
                    value : "log_critical"
                }],
                default:true
            },
            {
                title : "log_date",
                type : "dateTime",
                field : "calendar"
            }
        ],
        headerNames : [
            {
                name : "icon",
                filter : "icon"
            }, {
                name : "id",
                orderBy : true
            }, {
                name : "message",
                title : "log_message"
            },{
                name : "traceLevelTranslate",
                title : "log_trace_level",
                translate : true
            },{
                name : "calendar",
                title : "log_date",
                filter : "date",
                orderBy : true,
                defaultOrder : true,
                desc : true
            }
        ],
        callBackForFilter : function(log) {
            switch (log.traceLevel) {
                case "Debug":
                    log.traceLevelTranslate = "log_debug";
                    log.icon = {
                        name : "fa-bug",
                        color : "#AAFFB8"
                    };
                    break;
                case "Event" :
                    log.traceLevelTranslate = "log_event";
                    log.icon = {
                        name : "fa-lightbulb-o",
                        color : "#21BF4A"
                    };
                    break;
                case "Warning" :
                    log.traceLevelTranslate = "log_warning";
                    log.icon = {
                        name : "fa-exclamation",
                        color : "#FFC620"
                    };
                    break;
                case "Error" :
                    log.traceLevelTranslate = "log_error";
                    log.icon = {
                        name : "fa-exclamation-triangle",
                        color : "#FF5B5D"
                    };break;
                case "Critical" :
                    log.traceLevelTranslate = "log_critical";
                    log.icon = {
                        name : "fa-fire",
                        color : "#FF050C"
                    };break;
            }
        },
        selectItemCallback : function (item) {
            $scope.currentLog = item;
            if (item.stackTrace) {
                $scope.stackTraceData.stack = item.stackTrace;
            }
        }
    };

});