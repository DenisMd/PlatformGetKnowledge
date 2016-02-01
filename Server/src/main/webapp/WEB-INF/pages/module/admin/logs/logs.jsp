<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/admin.css">

<div class="panel panel-default">
    <div class="panel-body">

        <md-input-container>
            <label>{{translate("log_traceLevel")}}</label>
            <md-select ng-model="traceLevel">
                <md-option ng-repeat="state in ['','Debug','Event','Warning','Error','Critical']" value="{{state}}">
                    {{translate(state == '' ? '' : "log_"+state)}}
                </md-option>
            </md-select>
        </md-input-container>
        <md-button ng-click="searchLogs(traceLevel)" class="md-raised">{{translate("search")}}</md-button>
    </div>
</div>


<div class="table-selector">
    <table class="table table-hover ">
        <caption>{{translate("logs")}} : {{countLogs + ' ' + translate("ofRecords")}}</caption>
        <thead>
        <tr>
            <th>
                {{translate("log_icon")}}
            </th>
            <th>
                {{translate("log_message")}}
            </th>
            <th ng-click="setLogOrder('traceLevel')">
                {{translate("log_traceLevel")}}
            </th>
            <th ng-click="setLogOrder('calendar')">
                {{translate("log_date")}}
            </th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="log in logs" ng-click="setCurrentItem(log)">
            <td><span class="fa fa-2x" ng-class="log.iconClassName"></span></td>
            <td>{{log.message}}</td>
            <td>{{log.traceLevel}}</td>
            <td>{{log.calendar | date:'medium'}}</td>
        </tr>
        <tr>
            <td colspan="4" ng-click="loadMore()" class="loadMore">
                {{translate("log_loadMore")}}
            </td>
        </tr>
        </tbody>
    </table>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('log_stackTrace')}}" ng-if="currentLog != null && currentLog.stackTrace != null">
            <md-content flex layout-padding>
                <md-button class="btn md-raised md-warn clipboard"  data-clipboard-text="{{currentLog.stackTrace}}">
                    {{translate("copyToClipBoard")}}
                </md-button>
                <p id="bar">
                    {{currentLog.stackTrace}}
                </p>
            </md-content>
        </md-tab>
    </md-tabs>
</md-content>


