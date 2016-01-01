<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/tasks.css">
<h1>{{translate("tasks")}}</h1>
<select name="singleSelect" ng-model="selectTask">
    <option value="0">{{translate("Complete")}}</option>
    <option value="1">{{translate("Failed")}}</option>
    <option value="2">{{translate("NotStarted")}}</option>
    <option value="3">{{translate("Runnable")}}</option>
</select>
<table class="table table-hover">
    <thead>
    <tr>
        <th>Service name</th>
        <th ng-click="setOrder('calendar')">Start date</th>
        <th>Name</th>
        <th ng-click="setOrder('traceLevel')">Task status</th>
    </tr>
    </thead>
    <tbody>
    <tr ng-repeat="task in tasks | filter:taskFilter" ng-click="setCurrentItem(task)">
        <td>{{task.serviceName}}</td>
        <td>{{task.startDate | date:'yyyy-MM-dd HH:mm:ss'}}</td>
        <td>{{task.taskName}}</td>
        <td>{{translate(task.taskStatus)}}</td>
    </tr>

    </tbody>
</table>
<module-template name="inputs/textPlain" data="currentItem.jsonData"></module-template>