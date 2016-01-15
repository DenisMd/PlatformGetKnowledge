<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/admin.css">

<div class="panel panel-default">
    <div class="panel-body">
        <md-input-container>
            <label>{{translate("task_status")}}</label>
            <md-select ng-model="taskStatus">
                <md-option ng-repeat="status in ['','Complete','Failed','NotStarted','Runnable']" value="{{status}}">
                    {{translate(status == '' ? '' : "task_"+status)}}
                </md-option>
            </md-select>
        </md-input-container>
        <md-button ng-click="searchTasks(taskStatus)" class="md-raised">{{translate("search")}}</md-button>
    </div>
</div>


<div class="table-selector">
    <table class="table table-hover ">
        <caption>{{translate("tasks")}} : {{countTasks + ' ' + translate("ofRecords")}}</caption>
        <thead>
        <tr>
            <th ng-click="setTaskOrder('id')">
                {{translate("id")}}
            </th>
            <th>
                {{translate("name")}}
            </th>
            <th ng-click="setTaskOrder('taskStatus')">
                {{translate("task_status")}}
            </th>
            <th ng-click="setTaskOrder('start_date')">
                {{translate("task_calendar")}}
            </th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="task in tasks" ng-click="setCurrentItem(task)">
            <td>{{task.id}}</td>
            <td>{{task.taskName}}</td>
            <td>{{task.taskStatus}}</td>
            <td>{{task.startDate | date:'medium'}}</td>
        </tr>
        <tr>
            <td colspan="4" ng-click="loadMore()" class="loadMore">
                {{translate("task_loadMore")}}
            </td>
        </tr>
        </tbody>
    </table>
</div>


<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('task_jsonData')}}" ng-if="currentTask != null && currentTask.jsonData != null">
            <md-content flex layout-padding>
                <p id="bar">
                    <div hljs hljs-source="toPrettyJSON(currentTask.jsonData, 2)"></div>
                </p>
            </md-content>
        </md-tab>
        <md-tab label="{{translate('task_stackTrace')}}" ng-if="currentTask != null && currentTask.stackTrace != null">
            <md-content flex layout-padding>
                <md-button class="btn md-raised md-warn"  data-clipboard-text="{{currentTask.stackTrace}}">
                    {{translate("copyToClipBoard")}}
                </md-button>
                <p id="bar">
                    {{currentTask.stackTrace}}
                </p>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>
