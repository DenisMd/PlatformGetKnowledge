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
                constants : ["Debug", "Event", "Warning", "Error", "Critical"]
            },
            {
                title : "log_date",
                type : "dateTime",
                field : "calendar"
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            }, {
                name : "message",
                title : "log_message"
            },{
                name : "traceLevel",
                title : "log_trace_level",
                orderBy : true
            },{
                name : "calendar",
                title : "log_date",
                filter : "date",
                orderBy : true
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentLog = item;
            if (item.stackTrace) {
                $scope.stackTraceData.stack = item.stackTrace;
            }
        }
    };

});