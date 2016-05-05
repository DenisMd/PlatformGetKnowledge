model.controller("tasksCtrl", function ($scope,className) {

    $scope.refreshForCodeMirror = false;
    
    $scope.codeMirroData = {
        readOnly: true , 
        lineNumbers: true,
        theme:'twilight',
        mode:'application/json'
    };
    
    //Информация для вывода stack trace при ошибке выполнения сервиса
    $scope.stackTraceData = {
        title : "task_stack_trace"
    };

    $scope.selectorData = {
        className   : className.tasks,
        tableName   :   "tasks",
        loadMoreTitle : "task_load_more",
        filters      : [
            {
                title : "task_name",
                type  : "text",
                field : "taskName"
            },
            {
                title : "task_service_name",
                type : "text",
                field : "serviceName"
            },
            {
                title : "id",
                type : "number",
                field : "id"
            },
            {
                title : "task_status",
                type : "enum",
                field : "taskStatus",
                constants : ["Complete" , "Failed", "NotStarted" , "Runnable"]
            },
            {
                title : "task_calendar",
                type : "dateTime",
                field : "startDate"
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            }, {
                name : "taskName",
                title : "task_name"
            },{
                name : "serviceName",
                title : "task_service_name"
            },{
                name : "taskStatus",
                title : "task_status",
                orderBy : true
            },{
                name : "startDate",
                title : "task_calendar",
                filter : "date",
                orderBy : true
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentTask = item;
            if (item.stackTrace) {
                $scope.stackTraceData.stack = item.stackTrace;
            }
            if (item.jsonData) {
                $scope.codeMirroData.value = $scope.toPrettyJSON(item.jsonData,2);
            }
            $scope.refreshForCodeMirror = !$scope.refreshForCodeMirror;
        }
    };

});