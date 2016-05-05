<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/resources/css/workflow/workflow.css">
<link rel="stylesheet" href="/resources/bower_components/codemirror/theme/twilight.css">
<script src="/resources/bower_components/codemirror/mode/javascript/javascript.js"></script>

<div class="selector-zone">
    <module-template name="selectors/serverSelector" data="selectorData"></module-template>
</div>

<md-content>
    <md-tabs md-dynamic-height md-border-bottom>
        <md-tab label="{{translate('task_json_data')}}" ng-if="currentTask != null && currentTask.jsonData != null">
            <md-content flex layout-padding>
                <div ui-codemirror="codeMirroData" ui-refresh="refreshForCodeMirror"></div>
            </md-content>
        </md-tab>
        <md-tab label="{{translate('task_stack_trace')}}" ng-if="currentTask != null && currentTask.stackTrace != null">
            <module-template name="components/stacktrace" data="stackTraceData"></module-template>
        </md-tab>
    </md-tabs>
</md-content>
