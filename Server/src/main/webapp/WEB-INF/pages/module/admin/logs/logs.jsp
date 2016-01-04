<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/logs.css">
<h1>{{translate("logs")}}</h1>
<select name="singleSelect" ng-model="selectEvent">
    <option value="0">{{translate("Debug")}}</option>
    <option value="1">{{translate("Event")}}</option>
    <option value="2">{{translate("Warning")}}</option>
    <option value="3">{{translate("Error")}}</option>
    <option value="4">{{translate("Critical")}}</option>
</select>
<table class="table table-hover">
    <thead>
    <tr>
        <th>icon</th>
        <th ng-click="setOrder('calendar')">Date</th>
        <th>Message</th>
        <th ng-click="setOrder('traceLevel')">Trace level</th>
    </tr>
    </thead>
    <tbody>
    <tr ng-repeat="log in logs | orderBy:order | filter:eventFilter" ng-click="setCurrentItem(log)">
        <td><span class="fa fa-2x" ng-class="log.iconClassName"></span></td>
        <td>{{log.calendar | date:'yyyy-MM-dd HH:mm:ss'}}</td>
        <td>{{log.message}}</td>
        <td>{{translate(log.traceLevel)}}</td>
    </tr>

    </tbody>
</table>
<module-template name="inputs/textPlain" data="currentItem.stackTrace"></module-template>


